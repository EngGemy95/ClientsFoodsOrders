package com.fci_zu_eng_gemy_95.foodsorders.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fci_zu_eng_gemy_95.foodsorders.R;

public class MenuViewHolder extends RecyclerView.ViewHolder  {

    public TextView txtMenuName;
    public ImageView imageView;


    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.name_of_menu);
        imageView = itemView.findViewById(R.id.menuImage);

    }


}
