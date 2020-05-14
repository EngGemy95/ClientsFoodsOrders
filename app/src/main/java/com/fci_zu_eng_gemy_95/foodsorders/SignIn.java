package com.fci_zu_eng_gemy_95.foodsorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    MaterialEditText edtPhone, edtPassword;
    Button btnSignIn;
    CheckBox checkBoxRemember;
    TextView forgetPassword;
    FirebaseDatabase database;
    DatabaseReference table_users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnAccessLogin);
        checkBoxRemember = findViewById(R.id.chbRemember);
        forgetPassword = findViewById(R.id.txtforgetPwd);

        //init Paper
        Paper.init(this);

        //initialize firebase
        database = FirebaseDatabase.getInstance();
        table_users = database.getReference("users");


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgetPwdDialog();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getApplicationContext())) {

                    // save user && password
                    if (checkBoxRemember.isChecked()) {
                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PASSWORD_KEY, edtPassword.getText().toString());
                    }

                    final ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
                    progressDialog.setMessage("Please Wait... ");
                    progressDialog.show();

                    table_users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            progressDialog.dismiss();

                            // check if the user not exist in database
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                //Get User information
                                Users user = dataSnapshot.child(edtPhone.getText().toString()).getValue(Users.class);
                                user.setPhone(edtPhone.getText().toString());

                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    {
                                        Intent homeIntent = new Intent(SignIn.this, Home.class);
                                        Common.current_user = user;
                                        startActivity(homeIntent);
                                        Toast.makeText(SignIn.this, "Login Successfully !", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(SignIn.this, "Wrong Password !", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignIn.this, "phone is incorrect , user not exist !!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignIn.this, "Please Check Your Internet Connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void showForgetPwdDialog() {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(this);
        builder.setTitle("Forget Password");
        builder.setMessage("Enter Your Secure Code");

        LayoutInflater inflater = getLayoutInflater();
        View forgetPwdView = inflater.inflate(R.layout.forget_password_layout, null);

        builder.setView(forgetPwdView);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhoneForgetPwd = forgetPwdView.findViewById(R.id.edtPhoneForgetPwdPage);
        final MaterialEditText edtSecureCode = forgetPwdView.findViewById(R.id.edtSecureCodeForgetPwdPage);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Common.isConnectedToInternet(getApplicationContext())) {
                    table_users.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Users user = dataSnapshot.child(edtPhoneForgetPwd.getText().toString().trim()).getValue(Users.class);
                            Log.d("Userss",user.getSecureCode());
                            if (edtSecureCode.getText().toString().equals(user.getSecureCode())) {
                                Toast.makeText(SignIn.this, "Your Password is : " + user.getPassword(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignIn.this, "Wrong Secure Code !", Toast.LENGTH_SHORT).show();
                                showForgetPwdDialog();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(SignIn.this, "Please Check Your Internet Connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
