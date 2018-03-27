package com.alliejc.wcstinder.util;

import android.content.Context;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.alliejc.wcstinder.trackmyswing.Dancer;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CacheUtil {

    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private ICacheProvider mCacheProvider;

    public Long getExpiryTime() {
        return mExpiryTime;
    }

    public void setExpiryTime(Long mExpiryTime) {
        this.mExpiryTime = mExpiryTime;
    }

    private Long mExpiryTime;

    public CacheUtil(Context context){
        mContext = context;
    }

    public void saveStringDataToCache(String content, String key) {
        if (mCacheProvider != null) {
            CacheProvider.Entry entry = new CacheProvider.Entry();
            if (getExpiryTime() != null) entry.ExpireTime = getExpiryTime();

            entry.key = key;
            entry.data = SerializerUtil.serialize(content.toString());
            mCacheProvider.put(entry);
        }
    }

    public void saveDataToCache(List<Dancer> dancers, String key) {
        if (mCacheProvider != null) {
            CacheProvider.Entry entry = new CacheProvider.Entry();
            if (getExpiryTime() != null) entry.ExpireTime = getExpiryTime();

            entry.key = key;
            entry.data = SerializerUtil.serialize(dancers);
            mCacheProvider.put(entry);
        }
    }

    public void initCacheProvider(String filename) {
        if (mCacheProvider == null) {
            if (mContext == null) return;
            mCacheProvider = new CacheProvider(mContext);
            File cacheDir = new File(mContext.getFilesDir(), filename);
            mCacheProvider.initialize(cacheDir, CacheProvider.DEFAULT_DISK_USAGE_BYTES);
        }
    }

    private ICacheProvider getCacheProvider() {
        return mCacheProvider;
    }

    public String getStringDataFromCache(Boolean ignoreExpire, String key){
        String dataContent = "";
        mCacheProvider = getCacheProvider();
        if (mCacheProvider != null) {
            //Check what items are in cache and add them to return array.
            CacheProvider.Entry entry = mCacheProvider.get(key, ignoreExpire);
            if (entry != null && entry.data != null && entry.data.length > 0) {
                dataContent = SerializerUtil.deserializeString(entry.data).toString();
                try {
                    return dataContent;
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return dataContent;
    }

    public List<Dancer> getDataFromCache(Boolean ignoreExpire, String key){
        List<Dancer> drugListContent = null;
        mCacheProvider = getCacheProvider();
        if (mCacheProvider != null) {
            //Check what items are in cache and add them to return array.
            CacheProvider.Entry entry = mCacheProvider.get(key, ignoreExpire);
            if (entry != null && entry.data != null && entry.data.length > 0) {

                drugListContent = SerializerUtil.deserialize(entry.data);
                try {
                    Log.i(TAG, "drugs read from cache - " + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                    return drugListContent;
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return drugListContent;
    }

    public void removeKey(String key){
        if (mCacheProvider != null) {
            mCacheProvider.removeKey(key);
        }
    }
}

