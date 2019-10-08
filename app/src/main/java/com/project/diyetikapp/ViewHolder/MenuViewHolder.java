package com.project.diyetikapp.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.diyetikapp.Interface.ItemClickListener;
import com.project.diyetikapp.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);
        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);
    itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false); //itemView yerine view yazmış ama bende neden olmadı? kontrol et --> parametre olan view yazılmış

    }
}
