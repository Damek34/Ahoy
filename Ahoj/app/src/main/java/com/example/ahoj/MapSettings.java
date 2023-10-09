package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Spinner;

import java.util.Locale;

public class MapSettings extends AppCompatActivity {

    Spinner mapTypesSpinner;
    SharedPreferences sharedPreferences;
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
        setContentView(R.layout.activity_map_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        intent = getIntent();

        mapTypesSpinner = findViewById(R.id.mapTypesSpinner);

    }

    public void exitSettings (View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }


    public void saveSettings(View view){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(mapTypesSpinner.getSelectedItemPosition() == 1){
            editor.putString("map_type", "hybrid");
            editor.apply();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 2){
            editor.putString("map_type", "normal");
            editor.apply();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 3){
            editor.putString("map_type", "satellite");
            editor.apply();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 4){
            editor.putString("map_type", "terrain");
            editor.apply();
        }



        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

}