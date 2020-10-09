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


        FilePickerConfig defaultConfig = FilePickerConfig.getDefaultConfig();
        defaultConfig.setImageLoader(new IPreviewImageLoader() {
            @Override
            public void loadPreviewImage(File sourceFile, ImageView previewImageView) {
                // 如果需要加载其他的类型的图片，预览图策略需要更改，以便显示预览图或者图标
                Glide.with(previewImageView.getContext())
                        .load(sourceFile)
                        .apply(new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.ic_empty))
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
                        try {
                            FilePickerUtils.getInstance()
                                    .setPickerCount(3)
                                    .setFilePickerType("image/*")
                                    .launchPicker(MainActivity.this, new FilePickerValueCallback() {
                                        @Override
                                        public void onPickResult(List<File> file) {
                                            Log.d("TAG", "回调" + Arrays.toString(file.toArray()));
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissionList) {

                    }
                });
    }
}
