package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.ahoj.Setup.setup;

import java.util.List;

import DatabaseFiles.CountryAge.CountryAgeDatabase;
import DatabaseFiles.CountryAge.User;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (authToken != null) {
            Intent intent = new Intent(LoadingScreen.this, MapActivityMain.class);
            intent.putExtra("activity", "user");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
            }, 1500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingScreen.this, setup.class));
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
            }, 1500);
        }

        }
    //}
}