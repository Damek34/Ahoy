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

public class AddLocalEvent extends AppCompatActivity {

    int page = 1;
    String nameV, descV, locationV, company_nameV, additionalV, countryV;
    int durationV = 0;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    Spinner countrySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        countrySpinner = (Spinner) findViewById(R.id.eventCountry);

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

    }

    public void exitAdd(View view) {
        startActivity(new Intent(AddLocalEvent.this, MapActivityMain.class));
    }

    public void addEvent(View view) {

        // Map<String, Object> eventValues = new HashMap<>();
        //   eventValues.put("EventName", "Urodziny traweczki");
        //  eventValues.put("EventDescription", "Wielka libacja");
        //  eventValues.put("EventCompanyName", "PowerStudio");
        //  eventValues.put("EventLocalization", "Sanok");
        boolean canBeAdded = true;


        EditText editName = (EditText) findViewById(R.id.event_name);
        EditText description = (EditText) findViewById(R.id.event_description);
        EditText location = (EditText) findViewById(R.id.event_location);
        EditText company_name = (EditText) findViewById(R.id.event_company_name);
        EditText duration = (EditText) findViewById(R.id.event_duration);
        EditText additional = (EditText) findViewById(R.id.event_additional_info);

        nameV = editName.getText().toString();
        descV = description.getText().toString();
        locationV = location.getText().toString();
        company_nameV = company_name.getText().toString();
        additionalV = additional.getText().toString();
      //  countryV = countrySpinner.getSelectedItem().toString();
        String durationStr = duration.getText().toString();


        if (nameV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi mieć nazwę!", Toast.LENGTH_LONG).show();
        }
        if (descV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi mieć opis!", Toast.LENGTH_LONG).show();
        }
        if (locationV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi mieć lokalizację!", Toast.LENGTH_LONG).show();
        }
        if (company_nameV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi zawierać nazwę firmy!", Toast.LENGTH_LONG).show();
        }
        if (durationStr.isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi trwać przynajmniej godzinę!", Toast.LENGTH_LONG).show();
        }

        if (canBeAdded) {
            Date date = new Date();
            long millis = System.currentTimeMillis();
            String date_and_time = date + " " + millis;
            durationV = Integer.parseInt(durationStr);

            String countryName = "";
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(countrySpinner.getSelectedItem().toString(), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (addresses.size() > 0) {
                countryName = addresses.get(0).getCountryName();
            }


            TextView add_announcement = findViewById(R.id.add_announcement);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            calendar.add(Calendar.HOUR, durationV);
           // calendar.add(Calendar.SECOND, 15);
          //  calendar.add(Calendar.MINUTE, durationV);




            //reference = database.getReference("Event/" + countryName);
            reference = database.getReference("Waiting");
            // reference.setValue(date_and_time);
            //AddEventInfo newEvent = new AddEventInfo(date_and_time, nameV, descV, locationV, company_nameV, calendar.getTime(), additionalV);
            AddEventInfo newEvent = new AddEventInfo(date_and_time, nameV, descV, locationV, company_nameV, calendar.getTime(), additionalV, countryName);
            reference.child(date_and_time).setValue(newEvent);

            Toast.makeText(this, add_announcement.getText().toString(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(AddLocalEvent.this, MapActivityMain.class));
        }




    }
}