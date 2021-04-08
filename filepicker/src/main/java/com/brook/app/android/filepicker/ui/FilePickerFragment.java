package com.brook.app.android.filepicker.ui;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brook.app.android.activityresult.ActivityResultUtil;
import com.brook.app.android.filepicker.R;
import com.brook.app.android.filepicker.core.FilePickerConfig;
import com.brook.app.android.filepicker.core.FilePickerValueCallback;
import com.brook.app.android.filepicker.core.IFileProvider;
import com.brook.app.android.filepicker.core.IPreviewImageLoader;
import com.brook.app.android.filepicker.util.ConfigPool;
import com.brook.app.android.filepicker.util.DisplayUtil;
import com.brook.app.android.permissionutil.PermissionUtil;

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
    private TextView mImageFolder;
    private IPreviewImageLoader mImageLoader;
    private IFileProvider mImageProvider;
    private FilePickerAdapter filePickerAdapter;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getLifecycle().addObserver(new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                public void onDestroy() {
                    performCallback();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        performCallback();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageFolder = view.findViewById(R.id.image_folder);
        mRecyclerView = view.findViewById(R.id.content_list);

        final ProgressBar loadingView = view.findViewById(R.id.loading);
        final View tvBack = view.findViewById(R.id.back);
        tvFinish = view.findViewById(R.id.finish);

        tvBack.setOnClickListener(this);
        mImageFolder.setOnClickListener(this);

        loadingView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);

        Bundle arguments = getArguments();
        if (arguments != null) {
            FilePickerConfig config = ConfigPool.getInstance().getConfig(arguments.getLong(CONFIG_TOKEN));
            mImageProvider = config.getProvider(config.getPickerMimeType());
            mImageLoader = config.getImageLoader();
            mConfigCallback = config.getCallback();
            mPickerCount = config.getPickerCount();


            tvFinish.setEnabled(false);
            tvFinish.setOnClickListener(this);

            if (mPickerCount > 1) {
                tvFinish.setVisibility(View.VISIBLE);
            } else {
                tvFinish.setVisibility(View.GONE);
            }

            if (mImageProvider == null) {
                // TODO 没有Provider来处理这个操作
                return;
            }

            mImageProvider.getAllFile(new FilePickerValueCallback() {
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
                            filePickerAdapter = new FilePickerAdapter(fileList, mCurrentPickerFileList, mImageLoader, FilePickerFragment.this);
                            mRecyclerView.setAdapter(filePickerAdapter);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(final View v) {
        if (R.id.back == v.getId() || R.id.finish == v.getId()) {
            performCallback();
            finishParentActivity();
        } else if (v.getId() == R.id.image_folder) {
            // 显示PopupWindow

            mImageProvider.getAllFolder(new FilePickerValueCallback() {
                @Override
                public void onPickResult(final List<File> file) {
                    showFolderListPopupWindow(file, v);
                }
            });
        }
    }

    private void showFolderListPopupWindow(List<File> file, final View v) {
        v.setSelected(true);

        View popupView = LayoutInflater.from(getNonNullContext()).inflate(R.layout.filepicker_layout_image_folder_popupwindow, null, false);

        RecyclerView folderList = popupView.findViewById(R.id.folder_list);
        int popupHeight = getActivity().getWindow().getDecorView().findViewById(android.R.id.content).getHeight() - v.getHeight();

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                DisplayUtil.getScreenWidth(getNonNullContext()),
                popupHeight
        );
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);


        folderList.setLayoutManager(new LinearLayoutManager(getNonNullContext(), LinearLayoutManager.VERTICAL, false));
        folderList.setAdapter(new FolderListAdapter(getNonNullContext(), mImageLoader, file, new FolderListAdapter.OnFolderItemClickListener() {
            @Override
            public void onItemClick(View itemView, String folderName, List<File> files, int position) {
                mImageFolder.setText(folderName);
                filePickerAdapter.setFileList(files);
                filePickerAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
            }
        }));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                v.setSelected(false);
            }
        });

        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, DisplayUtil.getScreenHeight(getNonNullContext()) - popupHeight);
    }

    private Context getNonNullContext() {
        Context context = this.getContext();
        if (context == null) {
            context = this.getActivity();
        }
        return context;
    }

    private void performCallback() {
        if (mConfigCallback != null) {
            mConfigCallback.onPickResult(mCurrentPickerFileList);
            mConfigCallback = null;
        }
    }

    @Override
    public void onItemClick(@NonNull View itemView, @NonNull File targetFile, int position) {
        if (position == 0) {
            PermissionUtil.with(this)
                    .permission(Manifest.permission.CAMERA)
                    .request(new PermissionUtil.PermissionCallback() {
                        @Override
                        public void onGranted() {
                            Context context = getContext();
                            if (context == null) {
                                context = getActivity();
                            }
                            if (context != null) {
                                File externalCacheDir = context.getExternalCacheDir();
                                if (externalCacheDir != null) {

                                    String extraCache = externalCacheDir.getAbsolutePath();
                                    final File cameraSavePath = new File(extraCache + "/" + System.currentTimeMillis() + ".jpg");

                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    Uri uri = null;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        uri = FileProvider.getUriForFile(context, context.getApplicationInfo().packageName + ".cacheprovider", cameraSavePath);
                                        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                        if (resInfoList != null) {
                                            for (ResolveInfo resolveInfo : resInfoList) {
                                                String packageName = resolveInfo.activityInfo.packageName;
                                                context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            }
                                        }
                                    } else {
                                        uri = Uri.fromFile(cameraSavePath);
                                    }

                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                                    ActivityResultUtil.with(FilePickerFragment.this)
                                            .startActivityForResult(intent, new ActivityResultUtil.Callback() {

                                                @Override
                                                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                                                    if (cameraSavePath.exists()) {
                                                        mCurrentPickerFileList.add(cameraSavePath);
                                                        performCallback();
                                                        finishParentActivity();
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onDenied(List<String> list) {

                        }
                    });
        } else {
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
                    performCallback();
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
    }

    private void finishParentActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

}
