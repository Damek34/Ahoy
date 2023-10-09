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
import android.widget.TextView;

import java.util.Locale;

public class MapSettings extends AppCompatActivity {

    TextView map_type_textview;
    Spinner mapTypesSpinner;
    SharedPreferences sharedPreferences;
    Intent intent;
    Boolean is_map_type_textview_visible = false;
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
        map_type_textview = findViewById(R.id.map_type_textview);

        map_type_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_map_type_textview_visible){
                    mapTypesSpinner.setVisibility(View.VISIBLE);
                    is_map_type_textview_visible = true;
                }
                else{
                    mapTypesSpinner.setVisibility(View.GONE);
                    is_map_type_textview_visible = false;
                }
            }
        });

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