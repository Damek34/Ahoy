package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import DatabaseFiles.CountryAge.CountryAgeDatabase;
import DatabaseFiles.CountryAge.User;
import DatabaseFiles.Setings.Settings;
import DatabaseFiles.Setings.SettingsDatabase;


public class setup extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //Setting country spinner
        Spinner countrySpinner = (Spinner) findViewById(R.id.userCountry);

        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();

        for(Locale locale: locales){
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);

        countrySpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.custom_spinner);



        //Setting age spinner
        Spinner ageSpinner = (Spinner) findViewById(R.id.userAge);

        List<Integer> age = new ArrayList<Integer>();
        for (int i = 10; i <= 100; i++) {
            age.add(Integer.valueOf(Integer.toString(i)));
        }

        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, age);
        ageSpinner.setAdapter(adapter2);
        adapter2.setDropDownViewResource(R.layout.custom_spinner);

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            }
        }




        }

        public void setUpAcc(View view){

            CountryAgeDatabase dbAgeCountry = Room.databaseBuilder(getApplicationContext(),
                    CountryAgeDatabase.class, "user-database").allowMainThreadQueries().build();


            SettingsDatabase dbSettings = Room.databaseBuilder(getApplicationContext(),
                    SettingsDatabase.class, "user-settings-database").allowMainThreadQueries().build();

            Spinner countrySpinner = (Spinner) findViewById(R.id.userCountry);
            Spinner ageSpinner = (Spinner) findViewById(R.id.userAge);

            User user = new User();
            user.setCountry(countrySpinner.getSelectedItem().toString());
            user.setAge((Integer) ageSpinner.getSelectedItem());

            dbAgeCountry.userDAO().insertAll(user);

            Settings settings = new Settings();
            settings.setMapType(1);

            dbSettings.settingsDAO().insertAll(settings);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(setup.this, MapActivityMain.class));
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
            }, 1500);


        }
    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    }
