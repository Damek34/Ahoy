package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class EnableInternetConnection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_internet_connection);
    }

    public void continueL(View view){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(EnableInternetConnection.this, MapActivityMain.class));
                overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
            }
        }, 1500);
    }
}