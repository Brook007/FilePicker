package com.brook.app.android.filepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.brook.app.android.filepicker.core.FilePickerConfig;
import com.brook.app.android.filepicker.core.FilePickerValueCallback;
import com.brook.app.android.filepicker.core.IFileProvider;

/**
 * @author Brook
 * @time 2020-03-14 10:29
 */
public class FilePickerUtils {

    public static final String TAG = "FilePicker";

    public static FilePickerUtils INSTANCE;
    private FilePickerConfig mConfig = null;

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

    public FilePickerUtils addFileProvider(IFileProvider provider) {
        checkConfig();
        this.mConfig.addFileProvider(provider);
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
                mConfig = FilePickerConfig.getDefaultConfig().clone();
            } catch (CloneNotSupportedException e) {
                mConfig = FilePickerConfig.createDefaultConfig();
                e.printStackTrace();
            }
        }
    }

    public FilePickerUtils setFilePickerType(String type) {
        checkConfig();
        this.mConfig.setPickerMimeType(type);
        return this;
    }

    public FilePickerUtils setFilePickerConfig(FilePickerConfig config) {
        this.mConfig = config;
        return this;
    }

    public void launchPicker(@NonNull Context context, @NonNull FilePickerValueCallback callback) {
        checkConfig();
        if (this.mConfig.getProvider(this.mConfig.getPickerMimeType()) == null) {
            Log.e(TAG, "file provider not found");
            return;
        }
        this.mConfig.setContext(context);
        this.mConfig.setCallback(callback);
        this.mConfig.startLaunchPickerUI();
    }
}
