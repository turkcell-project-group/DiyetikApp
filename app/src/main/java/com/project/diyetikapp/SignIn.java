package com.project.diyetikapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.diyetikapp.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btnSingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword =(MaterialEditText)findViewById(R.id.edtPassword);
        edtPhone =(MaterialEditText)findViewById(R.id.edtPhone);
        btnSingIn = (Button)findViewById(R.id.btnSignIn);


        //Init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Check if user not exist in database
                        if(dataSnapshot.child(edtPhone.getText().toString()).exists()){


                            // Get user information
                            mDialog.dismiss();

                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                            if(user.getPassword().equals(edtPassword.getText().toString())){
                                Toast.makeText(SignIn.this,"Sing in successfully!",Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(SignIn.this, "Sing in failed!", Toast.LENGTH_SHORT).show();

                            } } else{
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "User not exist in Database", Toast.LENGTH_SHORT).show();


                        }
                    }

                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



    }
}

