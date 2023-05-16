package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
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


        CountryAgeDatabase db = Room.databaseBuilder(getApplicationContext(),
                CountryAgeDatabase.class, "user-database").allowMainThreadQueries().build();


        List<User> userList = db.userDAO().getAllUsers();


        String test = (db.userDAO().isExists().toString().toLowerCase());

        if (test == "true") {
            //  startActivity(new Intent(LoadingScreen.this, MapActivityMain.class));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingScreen.this, MapActivityMain.class));
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
            }, 1500);
        } else {

            //     startActivity(new Intent(LoadingScreen.this, setup.class));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingScreen.this, setup.class));
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
            }, 1500);
        }
    }
}