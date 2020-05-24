package com.brook.app.android.filepicker.core;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.MimeTypeFilter;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Brook
 * @time 2020-03-14 13:59
 */
public class DefaultImageFileProvider implements IFileProvider {

    private FilePickerConfig config;

    @Override
    public void injectConfig(FilePickerConfig config) {
        this.config = config;
    }

    @Override
    public boolean handleMimeType(String mimeType) {
        String[] mimeTypeArray = mimeType.split("/");
        return "image".equalsIgnoreCase(mimeTypeArray[0]);
    }

    @Override
    public boolean handlerFile(File file) {
        String fileName = file.getName().toLowerCase();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return MimeTypeFilter.matches(config.getPickerMimeType(), MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix));
    }

    @Override
    public void getAllFile(FilePickerValueCallback callback) {
        // TODO 先进行权限检查
        List<File> allPhoto = getAllPhoto();
        callback.onPickResult(allPhoto);

        //        File storageDirectory = Environment.getExternalStorageDirectory();
        //        getOrderGroupFile(storageDirectory, callback);
    }

    @Override
    public void getAllFolder(final FilePickerValueCallback callback) {
        //        List<File> allPhoto = getAllPhoto();
        //        Set<File> parentDirFile = new LinkedHashSet<File>();
        //        for (File file : allPhoto) {
        //            parentDirFile.add(file.getParentFile());
        //        }
        //        callback.onPickResult(new ArrayList<>(parentDirFile));
        getAllFile(new FilePickerValueCallback() {
            @Override
            public void onPickResult(List<File> files) {
                Set<File> parentDirFile = new LinkedHashSet<File>();
                for (File file : files) {
                    parentDirFile.add(file.getParentFile());
                }
                callback.onPickResult(new ArrayList<>(parentDirFile));
            }
        });
    }


    @Override
    public void getOrderGroupFile(final File parentFile, final FilePickerValueCallback callback) {
        new Thread() {
            @Override
            public void run() {
                LinkedList<File> resultList = new LinkedList<>();
                getOrderGroupFile(parentFile, resultList);
                callback.onPickResult(resultList);
            }
        }.start();
    }

    private void getOrderGroupFile(File parentFile, List<File> resultList) {
        File noMediaFile = new File(parentFile, ".nomedia");
        if (noMediaFile.exists()) {
            // 不扫描nomedia文件夹
            return;
        }

        File[] files = parentFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getOrderGroupFile(file, resultList);
                } else {
                    if (handlerFile(file)) {
                        resultList.add(file);
                    }
                }
            }
        }
    }

    private List<File> getAllPhoto() {

        ContentResolver contentResolver = config.getContext().getContentResolver();

        List<File> photos = new ArrayList<>();

        String[] projection = new String[]{MediaStore.Images.ImageColumns.DATA};

        //asc 按升序排列
        //    desc 按降序排列
        //projection 是定义返回的数据，selection 通常的sql 语句，例如  selection=MediaStore.Images.ImageColumns.MIME_TYPE+"=? " 那么 selectionArgs=new String[]{"jpg"};
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns.DATE_MODIFIED + "  desc");

        if (cursor != null) {
            String filePath;
            while (cursor.moveToNext()) {
                filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                File fileItem = new File(filePath);
                photos.add(fileItem);
            }
            cursor.close();
            cursor = null;
        }

        return photos;
    }
}
