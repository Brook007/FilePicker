package com.brook.app.android.filepicker.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brook.app.android.filepicker.R;
import com.brook.app.android.filepicker.core.IPreviewImageLoader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * @author Brook
 * @time 2020/5/25 11:16
 */
public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderHolder> {

    private final List<Pair<String, File[]>> mDirFiles;
    private final IPreviewImageLoader mImageLoader;
    private final OnFolderItemClickListener onItemClickListener;

    public FolderListAdapter(Context context, IPreviewImageLoader imageLoader, List<File> dir, OnFolderItemClickListener onItemClickListener) {
        this.mImageLoader = imageLoader;
        this.onItemClickListener = onItemClickListener;
        this.mDirFiles = new ArrayList<>();
        mDirFiles.add(null);

        for (File file : dir) {
            if (file.isDirectory()) {
                File[] files = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile();
                    }
                });
                if (files != null && files.length > 0) {
                    mDirFiles.add(new Pair<String, File[]>(file.getName(), files));
                }
            }
        }

        List<File> allImageFile = new ArrayList<File>();
        for (int i = 1; i < mDirFiles.size(); i++) {
            Pair<String, File[]> dirFile = mDirFiles.get(i);
            allImageFile.addAll(Arrays.asList(dirFile.second));
        }
        mDirFiles.set(0, new Pair<String, File[]>(context.getResources().getString(R.string.all_image), allImageFile.toArray(new File[]{})));
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FolderHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filepicker_item_image_folder, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        Pair<String, File[]> files = this.mDirFiles.get(position);
        mImageLoader.loadPreviewImage(files.second == null || files.second.length == 0 ? null : files.second[0], viewHolder.mFolderPreview);
        viewHolder.mFolderName.setText(files.first);
        viewHolder.mFolderDescription.setText(String.format(Locale.getDefault(), "共%d张照片", files.second == null ? 0 : files.second.length));
        viewHolder.mItemClickListener = onItemClickListener;
        viewHolder.position = position;
        viewHolder.fileDir = files.second;
        viewHolder.itemView.setOnClickListener(viewHolder);
    }

    @Override
    public int getItemCount() {
        return mDirFiles.size();
    }

    public interface OnFolderItemClickListener {
        void onItemClick(View itemView, String folderName, List<File> files, int position);
    }

    protected static class FolderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mFolderPreview;
        private final TextView mFolderName;
        private final TextView mFolderDescription;
        private OnFolderItemClickListener mItemClickListener;
        private File[] fileDir;
        private int position;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            mFolderPreview = itemView.findViewById(R.id.folder_preview);
            mFolderName = itemView.findViewById(R.id.folder_name);
            mFolderDescription = itemView.findViewById(R.id.folder_description);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, mFolderName.getText().toString(), Arrays.asList(fileDir), position);
            }
        }
    }
}