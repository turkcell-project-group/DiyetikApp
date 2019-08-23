package com.project.diyetikapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.project.diyetikapp.Interface.ItemClickListener;
import com.project.diyetikapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress= (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId= (TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone= (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus= (TextView)itemView.findViewById(R.id.order_status);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(itemView,getAdapterPosition(),false);
    }
}
