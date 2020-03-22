package com.brook.app.android.filepicker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.brook.app.android.filepicker.core.FilePickerConfig;
import com.brook.app.android.filepicker.core.FilePickerValueCallback;
import com.brook.app.android.filepicker.core.IFileProvider;

/**
 * @author Brook
 * @time 2020-03-14 10:29
 */
public class FilePickerUtils {

    public static FilePickerUtils INSTANCE;

    private FilePickerUtils() {

    }

    public static FilePickerUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (FilePickerUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FilePickerUtils();
                }
            }
        }
        return INSTANCE;
    }

    private FilePickerConfig mConfig = null;

    public FilePickerUtils setFileProvider(IFileProvider provider) {
        checkConfig();
        this.mConfig.setFileProvider(provider);
        return this;
    }

    public FilePickerUtils setPickerCount(int pickerCount) {
        checkConfig();
        this.mConfig.setPickerCount(pickerCount);
        return this;
    }

    private void checkConfig() {
        if (mConfig == null) {
            try {
                mConfig = FilePickerConfig.DEFAULT_CONFIG.clone();
            } catch (CloneNotSupportedException e) {
                mConfig = FilePickerConfig.createDefaultConfig();
                e.printStackTrace();
            }
        }
    }

    public FilePickerUtils setFilePickerType(FilePickerConfig.Type type) {
        checkConfig();
        this.mConfig.setPickerType(type);
        return this;
    }

    public FilePickerUtils setFilePickerConfig(FilePickerConfig config) {
        this.mConfig = config;
        return this;
    }

    public void launchPicker(@NonNull Context context, @NonNull FilePickerValueCallback callback) {
        if (mConfig != null) {
            mConfig.setContext(context);
            mConfig.setCallback(callback);
            mConfig.startLaunchPickerUI();
        }
    }
}
