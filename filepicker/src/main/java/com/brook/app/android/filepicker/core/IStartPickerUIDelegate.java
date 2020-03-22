package com.brook.app.android.filepicker.core;

import android.content.Context;

/**
 * @author Brook
 * @time 2020-03-14 13:45
 */
public interface IStartPickerUIDelegate {
    void launchPickerUI(Context context, FilePickerConfig filePickerConfig);
}
