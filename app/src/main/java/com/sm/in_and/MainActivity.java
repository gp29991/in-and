package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import helpers.AuthHelper;
import helpers.LoginHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        redirect();
    }

    public void redirect(){
        String authenticationResult = AuthHelper.authorize(LoginHelper.getToken(this), this);
        if(authenticationResult == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(this, MainPageActivity.class);
            startActivity(i);
            finish();
        }
    }
}