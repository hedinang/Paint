package com.example.myapplication.viewHolder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Interface.ViewOnClick;
import com.example.myapplication.R;
import com.example.myapplication.common.Common;

public class FileViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    ViewOnClick viewOnClick;
    public void setViewOnClick(ViewOnClick viewOnClick){
        this.viewOnClick = viewOnClick;
    }
    public FileViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
        itemView.setOnClickListener(view -> {
            viewOnClick.onClick(getAdapterPosition());
        });
        itemView.setOnCreateContextMenuListener((contextMenu, v, contextMenuInfo) -> contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE));
    }
}
