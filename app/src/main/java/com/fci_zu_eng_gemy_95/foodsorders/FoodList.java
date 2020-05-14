package com.fci_zu_eng_gemy_95.foodsorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Databases.Databases;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Food;
import com.fci_zu_eng_gemy_95.foodsorders.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView_food;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference FoodRef;
    FirebaseRecyclerOptions<Food> options;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter = null;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter = null;
    FirebaseRecyclerOptions<Food> searchOptions;

    //local database
    Databases localDatabase ;

    String CategoryId = "";
    MaterialSearchBar materialSearchBar;
    List<String> suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        recyclerView_food = findViewById(R.id.recyclerview_food);
        recyclerView_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_food.setLayoutManager(layoutManager);

        //local database
        localDatabase = new Databases(this);

        database = FirebaseDatabase.getInstance();
        FoodRef = database.getReference("Foods");
        FoodRef.keepSynced(true);

        if (getIntent() != null)
            CategoryId = getIntent().getStringExtra("CategoryId");

        if (!CategoryId.isEmpty() && CategoryId != null) {
            if (Common.isConnectedToInternet(this)) {
                loadFoodsData(CategoryId);
            }else {
                Toast.makeText(FoodList.this, "Please Check Your Internet Connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        materialSearchBar = findViewById(R.id.material_search);
        materialSearchBar.setHint("Enter Your Food");

        loadSuggestion();

        materialSearchBar.setLastSuggestions(suggestList);
        //materialSearchBar.setCardViewElevation(15);

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    recyclerView_food.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }



     void startSearch(CharSequence text) {
        searchOptions = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(FoodRef.orderByChild("name").equalTo(text.toString()), Food.class).build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(searchOptions) {
            @Override
            protected void onBindViewHolder(FoodViewHolder holder, final int position, final Food model) {

                holder.foodName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.image);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent FoodDetialsIntent = new Intent(FoodList.this, FoodDetails.class);
                        //get Food Key and send to new Activity t   o show detials
                        FoodDetialsIntent.putExtra("foodId", searchAdapter.getRef(position).getKey());
                        startActivity(FoodDetialsIntent);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        recyclerView_food.setAdapter(searchAdapter);
        searchAdapter.startListening();
    }

    private void loadSuggestion() {
        FoodRef.orderByChild("menuId").equalTo(CategoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Food foodItem = data.getValue(Food.class);
                    suggestList.add(foodItem.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void loadFoodsData(String categoryId) {
        // As select 8 from Foods where menuId = CategoryId
        options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(FoodRef.orderByChild("menuId").equalTo(categoryId), Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final FoodViewHolder holder, final int position, final Food model) {
                holder.foodName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.image);
                //Glide.with(FoodList.this).load(model.getImage()).into(holder.image);

                if (localDatabase.isFavourite(adapter.getRef(position).getKey())){
                    holder.favourite_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
               /* else {
                    holder.favourite_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }*/
               holder.favourite_image.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (!localDatabase.isFavourite(adapter.getRef(position).getKey())){
                          localDatabase.addToFavourites(adapter.getRef(position).getKey());
                           holder.favourite_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                           Toast.makeText(FoodList.this, model.getName()+" added to favourite", Toast.LENGTH_SHORT).show();
                       }else{
                           localDatabase.removeFromFavourites(adapter.getRef(position).getKey());
                           holder.favourite_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                           Toast.makeText(FoodList.this, model.getName()+" removed from favourite", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent FoodDetialsIntent = new Intent(FoodList.this, FoodDetails.class);
                        //get Food Key and send to new Activity to show detials
                        FoodDetialsIntent.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(FoodDetialsIntent);
                    }
                });
            }
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }
        };
        recyclerView_food.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
        if (searchAdapter != null)
            searchAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
        if (searchAdapter != null)
            searchAdapter.stopListening();
    }
}
