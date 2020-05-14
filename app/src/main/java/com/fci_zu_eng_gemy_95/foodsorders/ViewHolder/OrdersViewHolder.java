package com.fci_zu_eng_gemy_95.foodsorders.ViewHolder;

import android.view.View;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.fci_zu_eng_gemy_95.foodsorders.R;

    public class OrdersViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId , txtOrderStatus , txtOrderPhone , txtOrderAddress ;

    public OrdersViewHolder(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_adress);
    }

}
