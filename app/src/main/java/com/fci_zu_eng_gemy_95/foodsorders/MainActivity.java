package com.fci_zu_eng_gemy_95.foodsorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    FButton btnSignUp, btnLogin;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLoginID);
        btnSignUp = findViewById(R.id.btnSignUpID);

        txtSlogan = findViewById(R.id.txtSlogan);

        //init Paper
        Paper.init(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(typeface);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignIn.class));
            }
        });

        //Check Remember
        String user = Paper.book().read(Common.USER_KEY);
        String password = Paper.book().read(Common.PASSWORD_KEY);
        if (user != null && password != null) {
            if (!user.isEmpty() && !password.isEmpty()){
                login(user,password);
            }
        }

    }

    private void login(final String userPhone, final String password) {
        //initialize firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = database.getReference("users");

        if (Common.isConnectedToInternet(getApplicationContext())) {

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait... ");
            progressDialog.show();

            table_users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    progressDialog.dismiss();

                    // check if the user not exist in database
                    if (dataSnapshot.child(userPhone).exists()) {

                        //Get User information
                        Users user = dataSnapshot.child(userPhone).getValue(Users.class);
                        user.setPhone(userPhone);

                        if (user.getPassword().equals(password)){
                            {
                                Intent homeIntent = new Intent(MainActivity.this, Home.class);
                                Common.current_user = user;
                                startActivity(homeIntent);
                                Toast.makeText(MainActivity.this, "Login Successfully !", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "phone is incorrect , user not exist !!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            Toast.makeText(MainActivity.this, "Please Check Your Internet Connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
