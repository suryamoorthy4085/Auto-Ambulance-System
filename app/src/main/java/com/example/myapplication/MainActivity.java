package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private TextView animateText;
    private ImageView siren;

    Animation animateNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               Intent intent = new Intent(MainActivity.this, LoginActivity.class);
               startActivity(intent);
               finish();
           }
        }, 5000);

        init();
    }
    private void init(){

        animateText = (TextView) findViewById(R.id.textView2);
        siren = (ImageView) findViewById(R.id.imageView);
        animateNow = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.up_animation);
        animateText.setAnimation(animateNow);

        Glide.with(this).load(R.drawable.siren).into(siren);
    }
}
