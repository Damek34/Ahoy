package com.chatoyment.ahoyapp.Setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.chatoyment.ahoyapp.R;

import java.util.Locale;

public class RegisterOrLogin extends AppCompatActivity {

    Intent activity_intent;

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
        setContentView(R.layout.activity_register_or_login);

        activity_intent = getIntent();
    }

    public void exit(View view){
        startActivity(new Intent(RegisterOrLogin.this, setup.class));
    }

    public void register(View view){
        if(activity_intent.getStringExtra("activity").equals("main")){
            Intent intent = new Intent(RegisterOrLogin.this, TellSomething.class);
            intent.putExtra("activity", "main");

            startActivity(intent);
        }

        else
        {
            Intent intent = new Intent(RegisterOrLogin.this, RegisterUser.class);
            intent.putExtra("activity", "user");

            startActivity(intent);
        }
    }
    public void login(View view) {
        if (activity_intent.getStringExtra("activity").equals("main")) {
            Intent intent = new Intent(RegisterOrLogin.this, Login.class);
            intent.putExtra("activity", "main");

            startActivity(intent);
        } else {
            Intent intent = new Intent(RegisterOrLogin.this, LoginUser.class);
            intent.putExtra("activity", "user");

            startActivity(intent);
        }
    }
}