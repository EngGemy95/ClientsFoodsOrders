package com.fci_zu_eng_gemy_95.foodsorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Databases.Databases;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Food;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Order;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {

    //Food Details

    TextView foodName, price, description;
    ImageView food_image;
    FloatingActionButton btnCart;
    ElegantNumberButton btnQuantityFood;
    CollapsingToolbarLayout collapsingToolbarLayout;

    Food currentFood;

    //firebase
    FirebaseDatabase database;
    DatabaseReference foodsRef;

    String foodKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //init Food details variables
        foodName = findViewById(R.id.details_Food_name);
        price = findViewById(R.id.food_price);
        description = findViewById(R.id.details_food_description);
        food_image = findViewById(R.id.imgFood);
        btnCart = findViewById(R.id.floating_btn_cart);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        btnQuantityFood = findViewById(R.id.number_of_food);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpanededAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Databases(getBaseContext()).addToCart(
                        new Order(
                                foodKey,
                                currentFood.getName(),
                                btnQuantityFood.getNumber(),
                                currentFood.getPrice(),
                                currentFood.getDiscount())
                );
                Toast.makeText(getBaseContext(),"Added To Cart",Toast.LENGTH_SHORT).show();
            }
        });

        //init Firebase
        database = FirebaseDatabase.getInstance();
        foodsRef = database.getReference("Foods");

        if (foodKey != null) {
            foodKey = getIntent().getStringExtra("foodId");
        }
        if (!foodKey.isEmpty()) {
            if (Common.isConnectedToInternet(this)) {
                getFoodDetails(foodKey);
            }else {
                Toast.makeText(FoodDetails.this, "Please Check Your Internet Connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    void getFoodDetails(final String foodKey) {
        foodsRef.child(foodKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.get().load(currentFood.getImage()).into(food_image);
                foodName.setText(currentFood.getName());
                price.setText(currentFood.getPrice());
                description.setText(currentFood.getDescription());

                collapsingToolbarLayout.setTitle(currentFood.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
