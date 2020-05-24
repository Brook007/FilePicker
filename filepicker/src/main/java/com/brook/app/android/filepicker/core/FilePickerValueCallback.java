package com.brook.app.android.filepicker.core;

import java.io.File;
import java.util.List;

/**
 * @author Brook
 * @time 2020-03-14 11:13
 */
public interface FilePickerValueCallback {
    void onPickResult(List<File> files);
}