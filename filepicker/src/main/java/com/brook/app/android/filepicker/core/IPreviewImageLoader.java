package com.brook.app.android.filepicker.core;

import android.widget.ImageView;

import java.io.File;

public interface IPreviewImageLoader {
    void loadPreviewImage(File sourceFile, ImageView previewImageView);
}
