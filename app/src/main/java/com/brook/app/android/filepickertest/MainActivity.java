package com.brook.app.android.filepickertest;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.brook.app.android.filepicker.FilePickerUtils;
import com.brook.app.android.filepicker.core.FilePickerConfig;
import com.brook.app.android.filepicker.core.FilePickerValueCallback;
import com.brook.app.android.filepicker.core.IPreviewImageLoader;
import com.brook.app.android.permissionutil.PermissionUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FilePickerConfig defaultConfig = FilePickerConfig.DEFAULT_CONFIG;
        defaultConfig.setImageLoader(new IPreviewImageLoader() {
            @Override
            public void loadPreviewImage(File sourceFile, ImageView previewImageView) {
                Glide.with(previewImageView.getContext())
                        .load(sourceFile)
                        .apply(new RequestOptions().centerCrop())
                        .into(previewImageView);
            }
        });
        defaultConfig.setPickerCount(20);
    }

    public void startPicker(View view) {
        PermissionUtil.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new PermissionUtil.PermissionCallback() {
                    @Override
                    public void onGranted() {
                        FilePickerUtils.getInstance()
                                .setPickerCount(1)
                                .launchPicker(MainActivity.this, new FilePickerValueCallback() {
                                    @Override
                                    public void onPickResult(List<File> file) {
                                        Log.d("Brook", "回调" + Arrays.toString(file.toArray()));
                                    }
                                });
                    }

                    @Override
                    public void onDenied(List<String> permissionList) {

                    }
                });
    }
}
