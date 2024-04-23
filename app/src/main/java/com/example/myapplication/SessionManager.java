package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME ="LOGIN";
    private static final String LOGIN ="IS_LOGIN";


    public SessionManager(Context context){

        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }


    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }

}
