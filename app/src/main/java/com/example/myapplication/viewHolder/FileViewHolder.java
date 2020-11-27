package com.example.myapplication.viewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Interface.ViewOnClick;
import com.example.myapplication.R;
import com.example.myapplication.common.Common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    ViewOnClick viewOnClick;

    public FileViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewOnClick.onClick(getAdapterPosition());
            }
        });
        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
            }
        });
    }
}
