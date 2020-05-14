package com.fci_zu_eng_gemy_95.foodsorders.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fci_zu_eng_gemy_95.foodsorders.R;

public class FoodViewHolder extends RecyclerView.ViewHolder {

    public TextView foodName;
    public ImageView image;
    public ImageView favourite_image;

    public FoodViewHolder(View itemView) {
        super(itemView);

        foodName = itemView.findViewById(R.id.name_of_food);
        image = itemView.findViewById(R.id.image_of_food);
        favourite_image = itemView.findViewById(R.id.fav);

    }


}
