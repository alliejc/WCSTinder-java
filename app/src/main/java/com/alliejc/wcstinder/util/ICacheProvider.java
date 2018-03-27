package com.alliejc.wcstinder.util;

import java.io.File;

/**
 * Created by acaldwell on 3/27/18.
 */

public interface ICacheProvider {
    void initialize(File pCacheDirectory, int pCacheSize);
    void clear();
    void removeKey(String key);
    CacheProvider.Entry get(String key, boolean pIgnoreExpire);
    void put(CacheProvider.Entry obj);

}
