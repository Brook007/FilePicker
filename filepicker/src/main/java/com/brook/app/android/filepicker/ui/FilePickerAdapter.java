package com.brook.app.android.filepicker.ui;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brook.app.android.filepicker.R;
import com.brook.app.android.filepicker.core.IPreviewImageLoader;
import com.brook.app.android.filepicker.util.DisplayUtil;

import java.io.File;
import java.util.List;

class FilePickerAdapter extends RecyclerView.Adapter {

    private List<File> mFileList;
    private List<File> mCurrentPickerFileList;
    private IPreviewImageLoader mImageLoader;
    private OnItemClickListener mOnItemClickListener;

    FilePickerAdapter(@NonNull List<File> files, @NonNull List<File> currentPickerFileList,
                      @NonNull IPreviewImageLoader imageLoader, @NonNull OnItemClickListener listener) {
        this.mFileList = files;
        this.mCurrentPickerFileList = currentPickerFileList;
        this.mImageLoader = imageLoader;
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filepicker_item_camera, viewGroup, false);
            int itemWidth = (int) ((DisplayUtil.getScreenWidth(viewGroup.getContext()) - DisplayUtil.dp2px(viewGroup.getContext(), 1) * 2) / 3);
            view.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, itemWidth));
            return new CameraItemHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filepicker_item_thumbnail, viewGroup, false);
            int itemWidth = (int) ((DisplayUtil.getScreenWidth(viewGroup.getContext()) - DisplayUtil.dp2px(viewGroup.getContext(), 1) * 2) / 3);
            view.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, itemWidth));
            view.setBackgroundColor(Color.rgb(50, 50, 50));
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int p) {
        int position = viewHolder.getAdapterPosition();
        int viewType = getItemViewType(position);
        if (viewType == 0) {
            CameraItemHolder cameraItemHolder = (CameraItemHolder) viewHolder;
            cameraItemHolder.clickListener = mOnItemClickListener;
            cameraItemHolder.itemView.setOnClickListener(cameraItemHolder);
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            File file = mFileList.get(position - 1);
            itemViewHolder.targetFile = file;
            itemViewHolder.clickListener = mOnItemClickListener;

            itemViewHolder.ivSelectState.setSelected(mCurrentPickerFileList.contains(file));

            if (mImageLoader != null) {
                mImageLoader.loadPreviewImage(file, itemViewHolder.ivThumbnail);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView ivThumbnail;
        public final ImageView ivSelectState;
        public File targetFile;
        public OnItemClickListener clickListener;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.thumbnail);
            ivSelectState = itemView.findViewById(R.id.select_state);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(v, targetFile, getAdapterPosition());
            }
        }
    }

    private static class CameraItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public OnItemClickListener clickListener;

        public CameraItemHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(v, null, getAdapterPosition());
            }
        }
    }
}