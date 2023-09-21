package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

public class EnableInternetConnection extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = sharedPreferences2.getString("selectedLanguage", null);

        if (savedLanguage.equals("en")) {
            Locale locale2 = new Locale("en");
            Locale.setDefault(locale2);
            Configuration config = new Configuration();
            config.locale = locale2;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            Locale myLocale = new Locale("en");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
        else if (savedLanguage.equals("pl")) {
            Locale locale2 = new Locale("pl");
            Locale.setDefault(locale2);
            Configuration config = new Configuration();
            config.locale = locale2;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            Locale myLocale = new Locale("pl");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

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