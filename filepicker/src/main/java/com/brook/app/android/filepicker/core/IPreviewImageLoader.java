package com.brook.app.android.filepicker.core;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.File;

public interface IPreviewImageLoader {
    void loadPreviewImage(@Nullable File sourceFile, ImageView previewImageView);
}
