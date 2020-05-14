package com.fci_zu_eng_gemy_95.foodsorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Requests;
import com.fci_zu_eng_gemy_95.foodsorders.ViewHolder.OrdersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrdersStatus extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requestsRef;
    FirebaseRecyclerOptions<Requests> options;

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Requests, OrdersViewHolder> adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requestsRef = database.getReference("requests");
        requestsRef.keepSynced(true);

        //init
        recyclerView = findViewById(R.id.recycler_order_status);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //if we start OrderStatus Activity from Home Activity
        //we will not put any extra ,  so we just loadOrder by phone from Common

        if (getIntent().getStringExtra("home").equals("noData")) {
            loadOrders(Common.current_user.getPhone());
        }else {
            loadOrders(getIntent().getStringExtra("userPhone"));
        }
    }

    private void loadOrders(String phone) {

        options = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(requestsRef.orderByChild("phone").equalTo(phone),Requests.class).build();

        adapter = new FirebaseRecyclerAdapter<Requests, OrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrdersViewHolder holder, int position, @NonNull Requests model) {
                holder.txtOrderId.setText(adapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderPhone.setText(model.getPhone());
                holder.txtOrderAddress.setText(model.getAddress());
            }

            @NonNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_status_layout,parent,false);
                return new OrdersViewHolder(view);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
