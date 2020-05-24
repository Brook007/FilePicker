package com.brook.app.android.filepicker.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.brook.app.android.filepicker.ui.FilePickerActivity;
import com.brook.app.android.filepicker.util.ConfigPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Brook
 * @time 2020-03-14 10:56
 */
public class FilePickerConfig implements Cloneable {

    private static FilePickerConfig DEFAULT_CONFIG;

    static {
        setDefaultConfig(createDefaultConfig());
    }

    private String pickerMimeType = null;
    private Set<IFileProvider> providers;
    private FilePickerValueCallback callback;
    private IStartPickerUIDelegate startPickerUIDelegate;
    private IPreviewImageLoader imageLoader;
    private Context mContext;
    private int pickerCount = 1;
    public FilePickerConfig() {
        providers = new HashSet<>();
        pickerMimeType = "image/*";
    }

    public static FilePickerConfig getDefaultConfig() {
        return DEFAULT_CONFIG;
    }

    public static void setDefaultConfig(FilePickerConfig defaultConfig) {
        DEFAULT_CONFIG = defaultConfig;
    }

    public static FilePickerConfig createDefaultConfig() {
        final FilePickerConfig config = new FilePickerConfig();
        config.addFileProvider(new DefaultImageFileProvider());
        config.setStartPickerUIDelegate(new IStartPickerUIDelegate() {
            @Override
            public void launchPickerUI(Context context, FilePickerConfig filePickerConfig) {
                Intent intent = new Intent(context, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.TOKEN, ConfigPool.getInstance().putConfig(filePickerConfig));
                context.startActivity(intent);
            }
        });
        return config;
    }

    public int getPickerCount() {
        return pickerCount;
    }

    public void setPickerCount(int pickerCount) {
        this.pickerCount = pickerCount;
    }

    public void addFileProvider(IFileProvider provider) {
        this.providers.add(provider);
    }

    public String getPickerMimeType() {
        return pickerMimeType;
    }

    public void setPickerMimeType(String type) {
        String[] mimeType = type.split("/");
        if (mimeType.length != 2) {
            throw new IllegalArgumentException("mineType error");
        }
        this.pickerMimeType = type;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    public IPreviewImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(IPreviewImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public IStartPickerUIDelegate getStartPickerUIDelegate() {
        return startPickerUIDelegate;
    }

    public void setStartPickerUIDelegate(IStartPickerUIDelegate startPickerUIDelegate) {
        this.startPickerUIDelegate = startPickerUIDelegate;
    }

    public FilePickerValueCallback getCallback() {
        return callback;
    }

    public void setCallback(FilePickerValueCallback callback) {
        this.callback = callback;
    }

    @Nullable
    public IFileProvider getProvider(String mineType) {
        for (IFileProvider provider : providers) {
            if (provider.handleMimeType(mineType)) {
                provider.injectConfig(this);
                return provider;
            }
        }
        return null;
    }

    public void startLaunchPickerUI() {
        if (startPickerUIDelegate != null) {
            startPickerUIDelegate.launchPickerUI(mContext, this);
        }
    }

    @Override
    public FilePickerConfig clone() throws CloneNotSupportedException {
        return (FilePickerConfig) super.clone();
    }
}