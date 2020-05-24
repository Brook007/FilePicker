package com.brook.app.android.filepicker.core;

import java.io.File;

/**
 * @author Brook
 * @time 2020-03-14 10:45
 */
public interface IFileProvider {

    void injectConfig(FilePickerConfig config);

    boolean handleMimeType(String mimeType);

    /**
     * 是否处理这个文件
     *
     * @param file
     * @return
     */
    boolean handlerFile(File file);

    /**
     * 获取全部文件
     *
     * @return
     */
    void getAllFile(FilePickerValueCallback callback);

    /**
     * 获取全部文件夹
     *
     * @param callback
     */
    void getAllFolder(FilePickerValueCallback callback);

    /**
     * 获取文件夹下的全部文件
     *
     * @param parerFile
     * @return
     */
    void getOrderGroupFile(File parerFile, FilePickerValueCallback callback);
}
