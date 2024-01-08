package com.chatoyment.ahoyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chatoyment.ahoyapp.R;
import com.example.ahoyapp.OnlyJava.AddAnnouncementInfo;
import com.example.ahoyapp.OnlyJava.CompanyAnnouncement;
import com.example.ahoyapp.OnlyJava.OnlineDate;
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
    TextView must_have_company, must_have_desc, must_have_hour, add, check_internet_connection, announcement_will_ends, duration_preview;

    Spinner country;
    String countryName;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Intent activity_intent;

    Intent social_intent;


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
        setContentView(R.layout.activity_add_announcement);

        OnlineDate.fetchDateAsync();

        social_intent = getIntent();

        announcement_desc = findViewById(R.id.announcement_description);
        announcement_company_name = findViewById(R.id.announcement_company_name);
        announcement_duration = findViewById(R.id.announcement_duration);
        announcement_additional = findViewById(R.id.announcement_additional_info);
        announcement_will_ends = findViewById(R.id.announcement_will_ends);

        must_have_company = findViewById(R.id.textViewMustHaveCompanyName);
        must_have_desc = findViewById(R.id.textViewMustHaveDesc);
        must_have_hour = findViewById(R.id.textViewMustLastAHour);
        add = findViewById(R.id.add_announcementPreview);
        duration_preview = findViewById(R.id.duration_preview);

        country = findViewById(R.id.announcementCountry);

        check_internet_connection = findViewById(R.id.check_internet_connection);

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


        activity_intent = getIntent();
        String test_name = activity_intent.getStringExtra("company_name");

        if(test_name != null){
            announcement_company_name.setText(test_name);
            announcement_desc.setText(activity_intent.getStringExtra("event_desc"));
            announcement_duration.setText(activity_intent.getStringExtra("duration"));
            announcement_additional.setText(activity_intent.getStringExtra("additional"));
        }

        announcement_duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(announcement_duration.getText().toString().trim().equals("")){
                    duration_preview.setText("");
                }
                else{
                    Date date = OnlineDate.getDate();
                    long millis = System.currentTimeMillis();
                    String date_and_time = date + " " + millis;


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.HOUR, Integer.parseInt(announcement_duration.getText().toString()));

                    duration_preview.setText(calendar.getTime().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    public void exitAdd(View view) {
        startActivity(new Intent(AddAnnouncement.this, SelectWhatToAdd.class));
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

            Toast.makeText(getApplicationContext(), check_internet_connection.getText().toString(), Toast.LENGTH_LONG).show();
        }

        if(addresses == null){
            return;
        }

        if (addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }



        //Date date = new Date();

        Date date = OnlineDate.getDate();
        long millis = System.currentTimeMillis();
        String date_and_time = date + " " + millis;


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, Integer.parseInt(announcement_duration.getText().toString()));
        //calendar.add(Calendar.SECOND, Integer.parseInt(announcement_duration.getText().toString()));


        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");

        if(social_intent.getStringExtra("isSocial").equals("true")){
            reference = database.getReference("WaitingSocialAnnouncements");
        }
        else{
            reference = database.getReference("WaitingAnnouncements");
        }


        AddAnnouncementInfo newAnnouncement = new AddAnnouncementInfo(date_and_time, announcement_company_name.getText().toString(), announcement_desc.getText().toString(), calendar.getTime(), announcement_additional.getText().toString(), countryName, savedEmail);
        reference.child(date_and_time).setValue(newAnnouncement);


        if(calendar.get(Calendar.MINUTE) < 10){
            Toast.makeText(this, add.getText().toString() + ". " + announcement_will_ends.getText().toString() + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
                    + ", " + calendar.get(Calendar.HOUR) + ":" + "0" + calendar.get(Calendar.MINUTE) , Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, add.getText().toString() + ". " + announcement_will_ends.getText().toString() + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
                    + ", " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) , Toast.LENGTH_LONG).show();
        }


        String modifiedEmail = savedEmail.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");



        reference = database.getReference("CompanyEmails/" + modifiedEmail);

        CompanyAnnouncement companyAnnouncement = new CompanyAnnouncement(date_and_time, calendar.getTime(), countryName);
        if(social_intent.getStringExtra("isSocial").equals("true")){
            reference.child("CompanySocialAnnouncement").setValue(companyAnnouncement);

        }
        else{
            reference.child("CompanyAnnouncement").setValue(companyAnnouncement);
        }


        Intent intent = new Intent(AddAnnouncement.this, MapActivityMain.class);
        intent.putExtra("activity", "main");
        startActivity(intent);



    }


    public void statute(View view){
        Intent statue = new Intent(AddAnnouncement.this, Statute.class);

        statue.putExtra("activity", "AddAnnouncement");
        statue.putExtra("event_desc", announcement_desc.getText().toString());
        statue.putExtra("company_name", announcement_company_name.getText().toString());
        statue.putExtra("duration", announcement_duration.getText().toString());
        statue.putExtra("additional", announcement_additional.getText().toString());
        if(social_intent.getStringExtra("isSocial").equals(true)){
            statue.putExtra("isSocial", "true");
        }
        else{
            statue.putExtra("isSocial", "false");
        }

        startActivity(statue);
    }

}