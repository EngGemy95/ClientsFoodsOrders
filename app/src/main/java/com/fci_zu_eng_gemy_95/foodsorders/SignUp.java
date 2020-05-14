package com.fci_zu_eng_gemy_95.foodsorders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fci_zu_eng_gemy_95.foodsorders.Common.Common;
import com.fci_zu_eng_gemy_95.foodsorders.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone , edtName,edtPassword , SecureCode;
    Button btnSignUp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);
        SecureCode = findViewById(R.id.edtSecureCode);

        btnSignUp = findViewById(R.id.btnSignUP);

        //initialize firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = database.getReference("users");

       btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (Common.isConnectedToInternet(getApplicationContext())) {
                   final ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                   progressDialog.setMessage("Please Wait... ");
                   progressDialog.show();

                   table_users.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           //check if user phone already exist
                           if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                               progressDialog.dismiss();
                               Toast.makeText(SignUp.this, "User phone already register !", Toast.LENGTH_SHORT).show();
                           } else {
                               progressDialog.dismiss();
                               Users users = new Users(
                                       edtName.getText().toString()
                                       , edtPassword.getText().toString()
                                       ,SecureCode.getText().toString());
                               table_users.child(edtPhone.getText().toString()).setValue(users);
                               Toast.makeText(SignUp.this, "User registered Successfully", Toast.LENGTH_SHORT).show();
                               finish();
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
               }else {
                   Toast.makeText(SignUp.this, "Please Check Your Internet Connection !!", Toast.LENGTH_SHORT).show();
                   return;
               }
           }
       });


    }
}
