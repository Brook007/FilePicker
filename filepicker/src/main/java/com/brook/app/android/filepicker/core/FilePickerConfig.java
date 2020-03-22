package com.brook.app.android.filepicker.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.brook.app.android.filepicker.ui.FilePickerActivity;
import com.brook.app.android.filepicker.util.ConfigPool;

/**
 * @author Brook
 * @time 2020-03-14 10:56
 */
public class FilePickerConfig implements Cloneable {

    public static FilePickerConfig DEFAULT_CONFIG = createDefaultConfig();

    public static FilePickerConfig createDefaultConfig() {
        final FilePickerConfig config = new FilePickerConfig();
        config.setFileProvider(new DefaultImageFileProvider());
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

    private Type pickerType = Type.PHOTO;
    private IFileProvider provider;
    private FilePickerValueCallback callback;
    private IStartPickerUIDelegate startPickerUIDelegate;
    private IPreviewImageLoader imageLoader;
    private Context mContext;
    private int pickerCount = 1;

    public int getPickerCount() {
        return pickerCount;
    }

    public void setPickerCount(int pickerCount) {
        this.pickerCount = pickerCount;
    }

    public void setFileProvider(IFileProvider provider) {
        this.provider = provider;
    }

    public void setPickerType(Type type) {
        this.pickerType = type;
    }

    public void setCallback(FilePickerValueCallback callback) {
        this.callback = callback;
    }

    public Type getPickerType() {
        return pickerType;
    }

    public void setContext(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    public void setStartPickerUIDelegate(IStartPickerUIDelegate startPickerUIDelegate) {
        this.startPickerUIDelegate = startPickerUIDelegate;
    }

    public Context getContext() {
        return mContext;
    }

    public void setImageLoader(IPreviewImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public IPreviewImageLoader getImageLoader() {
        return imageLoader;
    }

    public IStartPickerUIDelegate getStartPickerUIDelegate() {
        return startPickerUIDelegate;
    }

    public FilePickerValueCallback getCallback() {
        return callback;
    }

    public IFileProvider getProvider() {
        provider.injectConfig(this);
        return provider;
    }

    public void startLaunchPickerUI() {
        if (startPickerUIDelegate != null) {
            startPickerUIDelegate.launchPickerUI(mContext, this);
        }
    }

    public enum Type {
        ALL,
        MEDIA,
        PHOTO,
        VIDEO
    }

    @Override
    public FilePickerConfig clone() throws CloneNotSupportedException {
        return (FilePickerConfig) super.clone();
    }
}