package com.alliejc.wcstinder.util;

        import android.content.Context;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.StatFs;
        import android.util.Log;

        import java.io.EOFException;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.FilterInputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.LinkedHashMap;
        import java.util.List;
        import java.util.Map;

public class CacheProvider implements ICacheProvider {
    static String TAG = CacheProvider.class.getSimpleName();

    final Context mContext;

    public CacheProvider(Context pContext) {
        this.mContext = pContext;
    }


    /**
     * Default maximum disk usage in bytes.
     */
    public static final int DEFAULT_DISK_USAGE_BYTES = 2 * 1024 * 1024;


    /**
     * Map of the Key, CacheHeader pairs
     */
    private final Map<String, CacheHeader> mEntries = new LinkedHashMap<String, CacheHeader>(16, .75f, true);

    //Root directory where all cache is created
    private File mRootDirectory;


    @Override
    public synchronized void initialize(File pRootDirectory, int pCacheSize) {
        if (!pRootDirectory.exists()) {
            if (!pRootDirectory.mkdirs()) {
                Log.e(TAG, "Error creating caching directory");
            }
        }
        this.mRootDirectory = pRootDirectory;
        File[] files = pRootDirectory.listFiles();
        if (files == null) {
            return;
        }
        //Populate cache header
        CountingInputStream cis = null;
        for (File file : files) {
            try {
                cis = new CountingInputStream(new FileInputStream(file));
                CacheHeader header = CacheHeader.readHeader(cis);
                mEntries.put(header.key, header);
                cis.close();
            } catch (Exception e) {

            }
        }

    }


    @Override
    public void clear() {
        if (mRootDirectory.exists()) {
            File[] files = mRootDirectory.listFiles();
            for (File file : files) {
                file.delete();
            }
        }

    }

    @Override
    public void removeKey(String key) {
        File file = getFileForKey(key);
        if (file.exists()) {
            file.delete();
        }
        mEntries.remove(key);
    }

    @Override
    public synchronized Entry get(String key, boolean pIgnoreExpire) {
        if (mEntries.containsKey(key)) {
            CacheHeader header = mEntries.get(key);
            Entry entry = new Entry();
            entry.key = header.key;
            if (!pIgnoreExpire) {
                if (header.hasExpired()) {
                    return null;
                }
            }
            File file = getFileForKey(key);
            if (file.exists()) {
                CountingInputStream cis = null;
                try {
                    cis = new CountingInputStream(new FileInputStream(file));
                    CacheHeader.readHeader(cis); // eat header
                    // get remaining bytes
                    entry.data = streamToBytes(cis, (int) (file.length() - cis.bytesRead));
                    //validate the cache stored and cache returned has same size .. else return null
                    if (entry.data != null && header.size != entry.data.length) {
                        return null;
                    }
                    return entry;
                } catch (Exception e) {
                    Log.e(TAG, "Error in get CacheProvider" + e.getMessage());
                }
            }

        }
        return null;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public synchronized void put(Entry entry) {
        //if entry data has length as 0 or is null don't save any data
        if (entry == null || entry.data == null || entry.data.length == 0 || StringExtensions.isNullOrEmpty(entry.key)) {
            return;
        }
        //Check of the entry is in memory or disk
        if (mEntries.containsKey(entry.key)) {
            mEntries.remove(entry.key);
        }


        File file = getFileForKey(entry.key);
        FileOutputStream fos = null;
        if (file.exists()) {
            file.delete();
        }
        try {
            if (getUsableSpace(mRootDirectory) < entry.data.length) {
                prune(entry);
            }

            //Create a cache header for entry
            CacheHeader header = new CacheHeader(entry.key, entry);
            fos = new FileOutputStream(file);
            boolean suceess = header.writeHeader(fos);
            if (suceess) {
                fos.write(entry.data);
                fos.close();
                // add entry to in memory list
                mEntries.put(entry.key, header);
            } else {
                fos.close();
                Log.e(TAG, "Failed to write the header in cache");
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to write the value in cache" + e);

        }
    }


    public static long getUsableSpace(File path) {
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable for using as a
     * disk filename.
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private File getFileForKey(String key) {
        return new File(mRootDirectory, key);
    }


    private void prune(Entry pEntry) {
        // calculate size for incoming
        if (pEntry.data != null) {
            int sizeNeeded = pEntry.data.length;
            //get list of all cache entries and start removing the one last expire time
            List<CacheHeader> headers = new ArrayList(mEntries.values());
            Collections.sort(headers, new CacheHeaderComparator());
            // start removing entries
            int memoryFreed = 0;

            for (CacheHeader header : headers) {
                if (sizeNeeded < memoryFreed) {
                    break;
                } else {
                    removeKey(header.key);
                    memoryFreed += header.size;
                }
            }
        }
    }

    public class CacheHeaderComparator implements Comparator<CacheHeader> {
        public int compare(CacheHeader left, CacheHeader right) {
            if (left.ExpireTime < right.ExpireTime) {
                return -1;
            } else if (left.ExpireTime > right.ExpireTime) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    static class CacheHeader {
        public long size;
        public String key;
        public long ExpireTime;


        public CacheHeader() {

        }

        public CacheHeader(String key, Entry entry) {
            this.key = key;
            this.size = entry.data == null ? 0 : entry.data.length;
            this.ExpireTime = entry.ExpireTime;
        }

        /**
         * Writes the contents of this CacheHeader to the specified OutputStream.
         */
        public boolean writeHeader(OutputStream os) {
            try {
                writeString(os, key);
                writeLong(os, size);
                writeLong(os, ExpireTime);
                os.flush();
                return true;
            } catch (IOException e) {
                Log.d("%s", e.toString());
                return false;
            }
        }


        public static CacheHeader readHeader(InputStream is) throws IOException {
            CacheHeader entry = new CacheHeader();
            entry.key = readString(is);
            entry.size = readLong(is);
            entry.ExpireTime = readLong(is);
            return entry;
        }

        public boolean hasExpired() {
            return this.ExpireTime < System.currentTimeMillis();
        }

    }


    /**
     * Simple wrapper around {@link InputStream#read()} that throws EOFException
     * instead of returning -1.
     */
    private static int read(InputStream is) throws IOException {
        int b = is.read();
        if (b == -1) {
            throw new EOFException();
        }
        return b;
    }

    static void writeString(OutputStream os, String s) throws IOException {
        byte[] b = s.getBytes("UTF-8");
        writeLong(os, b.length);
        os.write(b, 0, b.length);
    }

    static String readString(InputStream is) throws IOException {
        int n = (int) readLong(is);
        byte[] b = streamToBytes(is, n);
        return new String(b, "UTF-8");
    }

    static void writeLong(OutputStream os, long n) throws IOException {
        os.write((byte) (n >>> 0));
        os.write((byte) (n >>> 8));
        os.write((byte) (n >>> 16));
        os.write((byte) (n >>> 24));
        os.write((byte) (n >>> 32));
        os.write((byte) (n >>> 40));
        os.write((byte) (n >>> 48));
        os.write((byte) (n >>> 56));
    }

    static long readLong(InputStream is) throws IOException {
        long n = 0;
        n |= ((read(is) & 0xFFL) << 0);
        n |= ((read(is) & 0xFFL) << 8);
        n |= ((read(is) & 0xFFL) << 16);
        n |= ((read(is) & 0xFFL) << 24);
        n |= ((read(is) & 0xFFL) << 32);
        n |= ((read(is) & 0xFFL) << 40);
        n |= ((read(is) & 0xFFL) << 48);
        n |= ((read(is) & 0xFFL) << 56);
        return n;
    }

    /**
     * Reads the contents of an InputStream into a byte[].
     */
    private static byte[] streamToBytes(InputStream in, int length) throws IOException {
        byte[] bytes = new byte[length];
        int count;
        int pos = 0;
        while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
            pos += count;
        }
        if (pos != length) {
            throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
        }
        return bytes;
    }

    public static class Entry {
        public String key;
        public byte[] data;
        public long ExpireTime;

        public boolean hasExpired() {
            return this.ExpireTime < System.currentTimeMillis();
        }
    }


    private static class CountingInputStream extends FilterInputStream {
        private int bytesRead = 0;

        private CountingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            int result = super.read();
            if (result != -1) {
                bytesRead++;
            }
            return result;
        }

        @Override
        public int read(byte[] buffer, int offset, int count) throws IOException {
            int result = super.read(buffer, offset, count);
            if (result != -1) {
                bytesRead += result;
            }
            return result;
        }
    }

}
