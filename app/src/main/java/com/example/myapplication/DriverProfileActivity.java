package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DriverProfileActivity extends AppCompatActivity {

    private EditText mName, mPhone;
    private ImageView mProfile;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference filepath;
    private String name;
    private String phone;
    private String mProfileImage;
    private String driverId;
    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        mName = (EditText) findViewById(R.id.name);
        mPhone = (EditText) findViewById(R.id.number);
        mProfile = (ImageView) findViewById(R.id.profileImage); 
        Button mConfirm = (Button) findViewById(R.id.confirm);
        Button mBack = (Button) findViewById(R.id.back);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

        }
        else {
            mAuth = FirebaseAuth.getInstance();
            driverId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId);
            getDriverInfo();
        }
        
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDriver();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    

    private void getDriverInfo(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String, Object>) snapshot.getValue();

                    if (map != null && map.get("name") != null) {
                        name = Objects.requireNonNull(map.get("name")).toString();
                        mName.setText(name);
                    }
                    if (map != null && map.get("phone") != null) {
                        phone = Objects.requireNonNull(map.get("phone")).toString();
                        mPhone.setText(phone);
                    }
                    if (map != null && map.get("profileImageUrl") != null) {
                        mProfileImage = Objects.requireNonNull(map.get("profileImageUrl")).toString();
                        Glide.with(getApplication()).load(mProfileImage).into(mProfile);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveDriver() {

        name = mName.getText().toString();
        phone = mPhone.getText().toString();
        Map driverInfo = new HashMap();
        driverInfo.put("name", name);
        driverInfo.put("phone", phone);
        mDatabase.updateChildren(driverInfo);
        if(resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_image").child(driverId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());

                            }
                            // Continue with the task to get the download URL
                            return filepath.getDownloadUrl();

                        }
                    });
                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", taskSnapshot.toString());
                    mDatabase.updateChildren(newImage);
                    finish();
                }
            });
        }
        else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==1 && resultCode== Activity.RESULT_OK){
            final Uri imageUri = Objects.requireNonNull(data).getData();
            resultUri = imageUri;
            mProfile.setImageURI(resultUri);
        }
    }
}