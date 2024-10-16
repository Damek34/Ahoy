package com.chatoyment.ahoyapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chatoyment.ahoyapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ManageSocialEvent extends AppCompatActivity {

    TextView name, ends, location, company, desc, additional, countryTextView, deleted, activityStatus, during_the_verification, active, activityRestrictions, available_to_everyone, age_restricted;
    String date_and_time = "", country = "", eventDescription = "", eventCompanyName = "", eventLocation = "", eventAdditional = "", eventName = "", email= "", email_date_and_time = "";
    Date eventDuration;

    Intent intent;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Boolean isFromEvent = true, eventRestrictions;

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
        setContentView(R.layout.activity_manage_social_event);

        intent = getIntent();


        name = findViewById(R.id.activity_event_name);
        ends = findViewById(R.id.activityEventEventEndsAt);
        location = findViewById(R.id.activityEventEventLocation);
        company = findViewById(R.id.activityEventEventCompanyName);
        desc = findViewById(R.id.activityEventEventDescription);
        additional = findViewById(R.id.activityEventEventAdditional);
        countryTextView = findViewById(R.id.activityEventEventCountry);
        deleted = findViewById(R.id.deleted);
        activityStatus = findViewById(R.id.activityStatus);
        during_the_verification = findViewById(R.id.during_the_verification);
        active = findViewById(R.id.active);
        activityRestrictions = findViewById(R.id.activityRestrictions);
        available_to_everyone = findViewById(R.id.available_to_everyone);
        age_restricted = findViewById(R.id.age_restricted);

        date_and_time = intent.getStringExtra("date_and_time");
        country = intent.getStringExtra("country");
        email = intent.getStringExtra("email");
        email_date_and_time = intent.getStringExtra("email_date_and_time");

        countryTextView.setText(countryTextView.getText().toString() + ": " + country);

        reference = database.getReference("SocialEvent/" + country + "/" + date_and_time);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    fromWaiting();
                    return;
                }
                else{
                    eventName = snapshot.child("event_name").getValue(String.class);
                    eventDescription = snapshot.child("event_description").getValue(String.class);
                    eventCompanyName = snapshot.child("event_company_name").getValue(String.class);
                    eventLocation = snapshot.child("event_localization").getValue(String.class);
                    eventDuration = snapshot.child("event_duration").getValue(Date.class);
                    eventAdditional = snapshot.child("event_additional").getValue(String.class);
                    eventRestrictions = snapshot.child("age_restricted").getValue(Boolean.class);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(eventDuration);
                int month = calendar.get(Calendar.MONTH) + 1;
                ends.setText(ends.getText().toString() + " " + eventDuration.getHours() + ":" + eventDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
              //  ends.setText(ends.getText().toString() + " " + eventDuration.getTime());
                name.setText(name.getText().toString() + " "+ eventName);
                location.setText(location.getText().toString() + " " +eventLocation);
                company.setText(company.getText().toString() + " " + eventCompanyName);
                desc.setText(desc.getText().toString() + " " + eventDescription);
                additional.setText(additional.getText().toString() + " " + eventAdditional);
                activityStatus.setText(activityStatus.getText() + ": " + active.getText().toString());

                if(eventRestrictions){
                    activityRestrictions.setText(activityRestrictions.getText().toString() + " " + age_restricted.getText().toString());
                }
                else{
                    activityRestrictions.setText(activityRestrictions.getText().toString() + " " + available_to_everyone.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void fromWaiting(){
        isFromEvent = false;
        reference = database.getReference("WaitingSocialEvents/" + date_and_time);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventName = snapshot.child("event_name").getValue(String.class);
                eventDescription = snapshot.child("event_description").getValue(String.class);
                eventCompanyName = snapshot.child("event_company_name").getValue(String.class);
                eventLocation = snapshot.child("event_localization").getValue(String.class);
                eventDuration = snapshot.child("event_duration").getValue(Date.class);
                eventAdditional = snapshot.child("event_additional").getValue(String.class);
                eventRestrictions = snapshot.child("age_restricted").getValue(Boolean.class);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(eventDuration);
                int month = calendar.get(Calendar.MONTH) + 1;
                ends.setText(ends.getText().toString() + " " + eventDuration.getHours() + ":" + eventDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
             //   ends.setText(ends.getText().toString() + " " + eventDuration.getTime());
                name.setText(name.getText().toString() + " "+ eventName);
                location.setText(location.getText().toString() + " " + eventLocation);
                company.setText(company.getText().toString() + " " + eventCompanyName);
                desc.setText(desc.getText().toString() + " " + eventDescription);
                additional.setText(additional.getText().toString() + " " + eventAdditional);
                activityStatus.setText(activityStatus.getText() + ": " + during_the_verification.getText().toString());

                if(eventRestrictions){
                    activityRestrictions.setText(activityRestrictions.getText().toString() + " " + age_restricted.getText().toString());
                }
                else{
                    activityRestrictions.setText(activityRestrictions.getText().toString() + " " + available_to_everyone.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(View view){
        if(isFromEvent){
            reference = database.getReference("SocialEvent/" + country + "/" + date_and_time);
            reference.removeValue();

            reference = database.getReference("CompanyEmails/" + email_date_and_time + "/CompanySocialEvent");
            reference.removeValue();
        }
        else{
            reference = database.getReference("WaitingSocialEvents/" + date_and_time);
            reference.removeValue();

            reference = database.getReference("CompanyEmails/" + email_date_and_time + "/CompanySocialEvent");
            reference.removeValue();
        }

        Toast.makeText(getApplicationContext(), deleted.getText().toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(ManageSocialEvent.this, Manage.class));
    }

    public void exit(View view){
        startActivity(new Intent(ManageSocialEvent.this, Manage.class));
    }
}