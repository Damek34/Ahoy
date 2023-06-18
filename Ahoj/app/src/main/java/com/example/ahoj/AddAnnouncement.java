package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahoj.OnlyJava.AddAnnouncementInfo;
import com.example.ahoj.OnlyJava.AddEventInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAnnouncement extends AppCompatActivity {

    EditText announcement_desc, announcement_company_name, announcement_duration, announcement_additional;
    TextView must_have_company, must_have_desc, must_have_hour, add;

    Spinner country;
    String countryName;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);

        announcement_desc = findViewById(R.id.announcement_description);
        announcement_company_name = findViewById(R.id.announcement_company_name);
        announcement_duration = findViewById(R.id.announcement_duration);
        announcement_additional = findViewById(R.id.announcement_additional_info);

        must_have_company = findViewById(R.id.textViewMustHaveCompanyName);
        must_have_desc = findViewById(R.id.textViewMustHaveDesc);
        must_have_hour = findViewById(R.id.textViewMustLastAHour);
        add = findViewById(R.id.add_announcementPreview);

        country = findViewById(R.id.announcementCountry);

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

        country.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.custom_spinner);
    }
    public void exitAdd(View view) {
        startActivity(new Intent(AddAnnouncement.this, MapActivityMain.class));
    }

    public void addAnnouncement(View view){
        if(announcement_company_name.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), must_have_company.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(announcement_desc.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), must_have_desc.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(announcement_duration.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), must_have_hour.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        countryName = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(country.getSelectedItem().toString(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }



        Date date = new Date();
        long millis = System.currentTimeMillis();
        String date_and_time = date + " " + millis;


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, Integer.parseInt(announcement_duration.getText().toString()));

        reference = database.getReference("WaitingAnnouncements");

        AddAnnouncementInfo newAnnouncement = new AddAnnouncementInfo(date_and_time, announcement_company_name.getText().toString(), announcement_desc.getText().toString(), calendar.getTime(), announcement_additional.getText().toString(), countryName);
        reference.child(date_and_time).setValue(newAnnouncement);

        Toast.makeText(this, add.getText().toString(), Toast.LENGTH_LONG).show();

        startActivity(new Intent(AddAnnouncement.this, MapActivityMain.class));



    }

}