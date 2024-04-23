package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverLoginActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-application-51bc6-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        final EditText Mobile = findViewById(R.id.Mobile);
        final EditText Password = findViewById(R.id.password);
        final Button login = findViewById(R.id.login);
        final Button register = findViewById(R.id.register);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        login.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String Mobiletxt = Mobile.getText().toString();
                final String passwordtxt = Password.getText().toString();

                if(Mobiletxt.isEmpty() || passwordtxt.isEmpty()){

                    Toast.makeText(DriverLoginActivity.this, "Please Enter your Mobile Number and Password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

                else{
                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(Mobiletxt)){
                                final String getPassword = snapshot.child(Mobiletxt).child("Password").getValue(String.class);

                                if(getPassword.equals(passwordtxt)){
                                    Toast.makeText(DriverLoginActivity.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(DriverLoginActivity.this, DriverMapActivity.class));
                                    finish();
                                }

                                else{
                                    Toast.makeText(DriverLoginActivity.this, "wrong Password", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                }
                            }
                            else{
                                Toast.makeText(DriverLoginActivity.this, "wrong Password", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(DriverLoginActivity.this, DriverRegisterActivity.class));
            }
        });

    }


}