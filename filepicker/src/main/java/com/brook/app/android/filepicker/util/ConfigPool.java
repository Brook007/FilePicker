package com.brook.app.android.filepicker.util;

import com.brook.app.android.filepicker.core.FilePickerConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brook
 * @time 2020-03-14 16:04
 */
public class ConfigPool {

    private static ConfigPool INSTANCE;
    private Map<Long, FilePickerConfig> pool;
    private long currentKey = Long.MAX_VALUE;

    private ConfigPool() {
        pool = new HashMap<>();
    }

    public static ConfigPool getInstance() {
        if (INSTANCE == null) {
            synchronized (ConfigPool.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConfigPool();
                }
            }
        }
        return INSTANCE;
    }

    public long putConfig(FilePickerConfig config) {
        long uniqueKey = generateUniqueKey();
        pool.put(uniqueKey, config);
        return uniqueKey;
    }

    public FilePickerConfig getConfig(long uniqueKey) {
        FilePickerConfig config = pool.get(uniqueKey);
        pool.remove(uniqueKey);
        return config;
    }

    private long generateUniqueKey() {
        while (pool.get(currentKey) != null) --currentKey;
        return currentKey;
    }
}
