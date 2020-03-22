package com.brook.app.android.filepicker.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brook.app.android.filepicker.R;
import com.brook.app.android.filepicker.core.FilePickerConfig;
import com.brook.app.android.filepicker.core.FilePickerValueCallback;
import com.brook.app.android.filepicker.core.IFileProvider;
import com.brook.app.android.filepicker.core.IPreviewImageLoader;
import com.brook.app.android.filepicker.util.ConfigPool;
import com.brook.app.android.filepicker.util.DisplayUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author Brook
 * @time 2020-03-14 11:01
 */
public class FilePickerFragment extends Fragment implements View.OnClickListener, OnItemClickListener {


    public static final String CONFIG_TOKEN = "token";

    private TextView tvFinish;
    private RecyclerView mRecyclerView;

    private int mPickerCount;
    private FilePickerValueCallback mConfigCallback;

    private List<File> mCurrentPickerFileList = new LinkedList<>();

    public static FilePickerFragment newInstance(long configToken) {
        FilePickerFragment pickerFragment = new FilePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CONFIG_TOKEN, configToken);
        pickerFragment.setArguments(bundle);
        return pickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filepicker_fragment_simple, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.content_list);
        final ProgressBar loadingView = view.findViewById(R.id.loading);
        final View tvBack = view.findViewById(R.id.back);
        tvFinish = view.findViewById(R.id.finish);
        tvBack.setOnClickListener(this);

        loadingView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        Bundle arguments = getArguments();
        IFileProvider provider;
        if (arguments != null) {
            FilePickerConfig config = ConfigPool.getInstance().getConfig(arguments.getLong(CONFIG_TOKEN));
            provider = config.getProvider();
            final IPreviewImageLoader imageLoader = config.getImageLoader();
            mConfigCallback = config.getCallback();
            mPickerCount = config.getPickerCount();


            tvFinish.setEnabled(false);
            tvFinish.setOnClickListener(this);

            if (mPickerCount > 1) {
                tvFinish.setVisibility(View.VISIBLE);
            } else {
                tvFinish.setVisibility(View.GONE);
            }

            provider.getAllFile(new FilePickerValueCallback() {
                @Override
                public void onPickResult(final List<File> fileList) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            mRecyclerView.addItemDecoration(
                                    new GridDividerItemDecoration(
                                            getContext(),
                                            (int) DisplayUtil.dp2px(getContext(), 1),
                                            0,
                                            false,
                                            false,
                                            Color.parseColor("#2A2A2A"))
                            );
                            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
                            mRecyclerView.setAdapter(new FilePickerAdapter(fileList, mCurrentPickerFileList, imageLoader, FilePickerFragment.this));
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.back == v.getId()) {
            finishParentActivity();
        } else if (R.id.finish == v.getId()) {
            mConfigCallback.onPickResult(mCurrentPickerFileList);
            finishParentActivity();
        }
    }


    @Override
    public void onItemClick(@NonNull View itemView, @NonNull File targetFile, int position) {
        if (mCurrentPickerFileList.contains(targetFile)) {
            mCurrentPickerFileList.remove(targetFile);
            itemView.setSelected(false);
        } else {
            if (mPickerCount > mCurrentPickerFileList.size()) {
                mCurrentPickerFileList.add(targetFile);
                itemView.setSelected(true);
            }
        }

        if (mCurrentPickerFileList.isEmpty()) {
            tvFinish.setText("发送");
        } else {
            tvFinish.setText(String.format(Locale.getDefault(), "发送(%d/%d)", mCurrentPickerFileList.size(), mPickerCount));
        }

        if (mPickerCount == mCurrentPickerFileList.size()) {
            if (mPickerCount == 1) {
                mConfigCallback.onPickResult(mCurrentPickerFileList);
                finishParentActivity();
            } else {
                tvFinish.setEnabled(true);
            }
        } else {
            if (mCurrentPickerFileList.isEmpty()) {
                tvFinish.setEnabled(false);
            } else {
                tvFinish.setEnabled(true);
            }
        }
    }

    private void finishParentActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

}
