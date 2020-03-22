package com.brook.app.android.filepicker.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.brook.app.android.filepicker.R;

/**
 * @author Brook
 * @time 2020-03-14 10:58
 */
public class FilePickerActivity extends AppCompatActivity {

    public static final String TOKEN = "token";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filepicker_activity_simple);

        long token = getIntent().getLongExtra(TOKEN, 0);
        if (token <= 0) {
            finish();
            return;
        }

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, FilePickerFragment.newInstance(token));
        fragmentTransaction.commitNowAllowingStateLoss();
    }
}
