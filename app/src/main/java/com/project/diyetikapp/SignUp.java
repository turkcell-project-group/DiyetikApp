package com.project.diyetikapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.diyetikapp.Common.Common;
import com.project.diyetikapp.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {
    MaterialEditText edtPhone, edtName,edtPassword,edtSecureCode;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        edtSecureCode = (MaterialEditText)findViewById(R.id.edtSecureCode);


        //Init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInterner(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if alredy user phone
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone number already register", Toast.LENGTH_SHORT).show();
                            } else {
                                mDialog.dismiss();
                                User user = new User(edtName.getText().toString(),
                                        edtPassword.getText().toString(),
                                        edtSecureCode.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //check if alredy user phon
                        }
                    });
                }
                else{
                    Toast.makeText(SignUp.this,"Lütfen bağlantınızı kontrol ediniz!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
