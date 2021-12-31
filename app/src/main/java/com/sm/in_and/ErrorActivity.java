package com.sm.in_and;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}