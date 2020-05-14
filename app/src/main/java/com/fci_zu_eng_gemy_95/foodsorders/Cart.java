package com.fci_zu_eng_gemy_95.foodsorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Databases.Databases;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Order;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Requests;
import com.fci_zu_eng_gemy_95.foodsorders.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {


    RecyclerView recyclerViewCartList ;
    RecyclerView.LayoutManager layoutManager ;

    FirebaseDatabase database;
    DatabaseReference requestsRef ;

    TextView txtTotalPrice ;
    FButton btnPlaceOrder ;

    List<Order> cart ;
    CartAdapter adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = new ArrayList<>();

        //init
        recyclerViewCartList = findViewById(R.id.recyclerViewCartList);
        recyclerViewCartList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCartList.setLayoutManager(layoutManager);

        txtTotalPrice = findViewById(R.id.resultTotalPrice);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0) {
                    showAlertDialog();
                }
                else {
                    Toast.makeText(Cart.this, "Your Cart is Empty !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Common.isConnectedToInternet(this)) {
            loadListFood();
        }else {
            Toast.makeText(this, "Please Check Your Internet Connection !!", Toast.LENGTH_SHORT).show();
        }

        //Firebase
        database = FirebaseDatabase.getInstance();
        requestsRef = database.getReference("requests");
        requestsRef.keepSynced(true);

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter Your Address : ");

        final EditText edtAddress = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(layoutParams);
        alertDialog.setView(edtAddress);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Requests requests = new Requests(
                        Common.current_user.getPhone(),
                        Common.current_user.getName(),
                        txtTotalPrice.getText().toString(),
                        edtAddress.getText().toString(),
                        cart
                );

                //Submit to Firebase
                //We will use CurrentTimeMillis as Key
                requestsRef.child(String.valueOf(System.currentTimeMillis())).setValue(requests);

                //Delete cart
                new Databases(getBaseContext()).cleanCart();
                Toast.makeText(getBaseContext(), R.string.thank_you_order_placed,Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void loadListFood(){
        cart = new Databases(this).getCarts();
        adapter = new CartAdapter(this,cart);
        adapter.notifyDataSetChanged();
        recyclerViewCartList.setAdapter(adapter);

        //calculate total price
        int total =0;
        for (Order order:cart){
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("en","US");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(numberFormat.format(total));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)){
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Databases(this).cleanCart();
        for (Order item : cart){
            new Databases(this).addToCart(item);
        }
        loadListFood();
    }
}
