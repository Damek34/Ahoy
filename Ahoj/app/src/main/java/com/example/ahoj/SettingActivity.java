package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import DatabaseFiles.Setings.SettingsDatabase;

public class SettingActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Spinner mapTypesSpinner = (Spinner) findViewById(R.id.mapTypesSpinner);
        List<String> mapTypesList = new ArrayList();


      //  mapTypesList.add(String.valueOf(R.string.terrain_map));
       // mapTypesList.add(test);
    //    mapTypesList.add(test);
     //   mapTypesList.add(test);

        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mapTypesList);
        //mapTypesSpinner.setAdapter(adapter);


    }

    public void exitSettings (View view){
        startActivity(new Intent(SettingActivity.this, MapActivityMain.class));
    }

    public void saveSettings(View view){

        SettingsDatabase dbSettings = Room.databaseBuilder(getApplicationContext(),
                SettingsDatabase.class, "user-settings-database").allowMainThreadQueries().build();

        Spinner mapTypesSpinner = (Spinner) findViewById(R.id.mapTypesSpinner);
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

        startActivity(new Intent(SettingActivity.this, MapActivityMain.class));

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
}