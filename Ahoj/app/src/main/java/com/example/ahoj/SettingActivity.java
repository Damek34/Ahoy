package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import DatabaseFiles.Setings.SettingsDatabase;

public class SettingActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_setting);



        intent = getIntent();

    }

    public void exitSettings (View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(SettingActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(SettingActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

    public void saveSettings(View view){

        SettingsDatabase dbSettings = Room.databaseBuilder(getApplicationContext(),
                SettingsDatabase.class, "user-settings-database").allowMainThreadQueries().build();

        Spinner mapTypesSpinner = (Spinner) findViewById(R.id.mapTypesSpinner);
        Spinner languageSpinner = findViewById(R.id.changelanguage);



        if(mapTypesSpinner.getSelectedItemPosition() == 1){
            dbSettings.settingsDAO().updateMapTypeToTHybrid();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 2){
            dbSettings.settingsDAO().updateMapTypeToNormal();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 3){
            dbSettings.settingsDAO().updateMapTypeToSatellite();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 4){
            dbSettings.settingsDAO().updateMapTypeToTerrain();
        }

        Locale locale = null;
        String selectedLanguage = languageSpinner.getSelectedItem().toString();
        if (selectedLanguage.equals("English") || selectedLanguage.equals("Angielski")) {
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

        } else if (selectedLanguage.equals("Polski") || selectedLanguage.equals("Polish")) {
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (selectedLanguage.equals("English") || selectedLanguage.equals("Angielski")) {
            editor.putString("selectedLanguage", "en");
            editor.apply();
        } else if (selectedLanguage.equals("Polski") || selectedLanguage.equals("Polish")) {
            editor.putString("selectedLanguage", "pl");
            editor.apply();
        }





        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(SettingActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(SettingActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }

    }

    public void reconnect(View view){
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 226);

            }
        }
    }

    public void logOut(View view){
        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", null);
        editor.apply();

        startActivity(new Intent(SettingActivity.this, LoadingScreen.class));
    }

    public void statute(View view){
        Intent intent1 = new Intent(SettingActivity.this, Statute.class);
        if(intent.getStringExtra("activity").equals("main")){
            intent1.putExtra("activity", "mainsettings");
            startActivity(intent1);
        }
        else{
            intent1.putExtra("activity", "usersettings");
            startActivity(intent1);
        }
    }
}