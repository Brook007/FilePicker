package com.brook.app.android.filepicker.core;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * @author Brook
 * @time 2020-03-14 10:49
 */
public interface IPreview {
    Bitmap getPreviewPicture(int width, int height);

    CharSequence getTitle();

    CharSequence getDescription();

    long getFileSize();

    String getAbsloultPath();

    Uri getUri();
}
