package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;

public class NoInternetActivity extends AppCompatActivity {

    private Button retry;
    private ImageView noInternet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        noInternet = (ImageView) findViewById(R.id.imageView3);
        Glide.with(this).load(R.drawable.connection).into(noInternet);
        retry = (Button) findViewById(R.id.button3);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activityNetwork = cm.getActiveNetworkInfo();
                if(activityNetwork != null){
                    Intent intent = new Intent(NoInternetActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Animatoo.animateSlideLeft(NoInternetActivity.this);
                }
            }
        });
    }
}