package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class EnableInternetConnection extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_internet_connection);

        intent = getIntent();
    }

    public void continueL(View view){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(intent.getStringExtra("activity").equals("loadingScreen")){
                    Intent intent = new Intent(EnableInternetConnection.this, LoadingScreen.class);
                    startActivity(intent);
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
                else{
                    if(intent.getStringExtra("activity").equals("user")){
                        Intent intent = new Intent(EnableInternetConnection.this, MapActivityMain.class);
                        intent.putExtra("activity", "user");
                        startActivity(intent);
                        overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                    }
                    else{
                        Intent intent = new Intent(EnableInternetConnection.this, MapActivityMain.class);
                        intent.putExtra("activity", "main");
                        startActivity(intent);
                        overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                    }
                }

            }
        }, 1500);
    }
}