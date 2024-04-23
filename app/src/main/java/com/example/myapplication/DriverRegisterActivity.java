package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverRegisterActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-application-51bc6-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        final EditText FullName = findViewById(R.id.FullName);
        final EditText Mobile_Number = findViewById(R.id.Phone);
        final EditText Email_Id = findViewById(R.id.Email);
        final EditText Vehicle_Number = findViewById(R.id.Vehicle);
        final EditText Password = findViewById(R.id.Codeword);
        final EditText Conf_Password = findViewById(R.id.Confpassword);
        final ProgressBar progressBar = findViewById(R.id.progressBar1);

        final Button register = findViewById(R.id.registerhr);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String fullName = FullName.getText().toString();
                final String emailId = Email_Id.getText().toString();
                final String mobileNumber = Mobile_Number.getText().toString();
                final String vehicleNumber = Vehicle_Number.getText().toString();
                final String password = Password.getText().toString();
                final String confPassword = Conf_Password.getText().toString();

                if(fullName.isEmpty() || emailId.isEmpty() || mobileNumber.isEmpty() || vehicleNumber.isEmpty()
                        || password.isEmpty() || confPassword.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DriverRegisterActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                }

                else if(!password.equals(confPassword)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DriverRegisterActivity.this, "password not matching", Toast.LENGTH_SHORT).show();
                }

                else{

                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(mobileNumber)){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(DriverRegisterActivity.this, "Number Already Registered", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                databaseReference.child("Users").child(mobileNumber).child("FullName").setValue(fullName);
                                databaseReference.child("Users").child(mobileNumber).child("Email_Id").setValue(emailId);
                                databaseReference.child("Users").child(mobileNumber).child("Vehicle_Number").setValue(vehicleNumber);
                                databaseReference.child("Users").child(mobileNumber).child("Password").setValue(password);

                                Toast.makeText(DriverRegisterActivity.this, "user Registered Successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });

    }
}