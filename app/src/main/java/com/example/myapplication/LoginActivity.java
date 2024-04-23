package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class LoginActivity extends AppCompatActivity {

    private Button userButton, driverButton;
    Animation userAnimate, driverAnimate;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        if(sessionManager.isLogin()){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            Animatoo.animateSlideRight(this);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();
        if(activityNetwork == null){
            Intent intent = new Intent(LoginActivity.this, NoInternetActivity.class);
            startActivity(intent);
            finish();
            Animatoo.animateSlideLeft(this);
        }

        userButton = (Button) findViewById(R.id.button2);
        driverButton = (Button) findViewById(R.id.button);

        userAnimate = AnimationUtils.loadAnimation(getApplicationContext(), com.blogspot.atifsoftwares.animatoolib.R.anim.animate_swipe_right_enter);
        driverAnimate = AnimationUtils.loadAnimation(getApplicationContext(), com.blogspot.atifsoftwares.animatoolib.R.anim.animate_swipe_right_enter);

        userButton.setAnimation(userAnimate);
        driverButton.setAnimation(driverAnimate);

    }

    public void userLoginClick(View view) {

        Intent intent = new Intent(LoginActivity.this, UserLoginActivity.class);
        startActivity(intent);
        Animatoo.animateSlideUp(this);

    }

    public void DriverLoginClick(View view) {
        Intent intent = new Intent(LoginActivity.this, DriverLoginActivity.class);
        startActivity(intent);
        Animatoo.animateSlideUp(this);
        finish();
    }
}