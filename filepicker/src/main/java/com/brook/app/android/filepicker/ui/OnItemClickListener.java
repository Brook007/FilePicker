package com.brook.app.android.filepicker.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;

/**
 * 点击监听
 *
 * @author brook
 * @time 2020年03月21日22:13
 */
interface OnItemClickListener {
    void onItemClick(@NonNull View itemView, @Nullable File targetFile, int position);
}
