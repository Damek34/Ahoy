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

public class ManageSocialAnnouncement extends AppCompatActivity {

    TextView ends, company, desc, additional, countryTextView, deleted, activityStatus, during_the_verification, active, activityRestrictions, available_to_everyone, age_restricted;
    String date_and_time = "", country = "", announcementDescription = "", announcementCompanyName = "", announcementAdditional = "", email= "", email_date_and_time = "";
    Date announcementDuration;

    Intent intent;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Boolean isFromAnnouncement = true, announcementRestrictions;


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
        setContentView(R.layout.activity_manage_social_announcement);

        intent = getIntent();

        date_and_time = intent.getStringExtra("date_and_time");
        country = intent.getStringExtra("country");
        email = intent.getStringExtra("email");
        email_date_and_time = intent.getStringExtra("email_date_and_time");

        ends = findViewById(R.id.activityAnnouncementEndsAt);
        company = findViewById(R.id.activity_announcement_company_name);
        desc = findViewById(R.id.activityAnnouncementDescription);
        additional = findViewById(R.id.activityAnnouncementAdditional);
        countryTextView = findViewById(R.id.activityAnnouncementCountry);
        deleted = findViewById(R.id.deleted);
        activityStatus = findViewById(R.id.activityStatus);
        during_the_verification = findViewById(R.id.during_the_verification);
        active = findViewById(R.id.active);
        activityRestrictions = findViewById(R.id.activityRestrictions);
        available_to_everyone = findViewById(R.id.available_to_everyone);
        age_restricted = findViewById(R.id.age_restricted);


        countryTextView.setText(countryTextView.getText().toString() + ": " + country);



        reference = database.getReference("SocialAnnouncement/" + country + "/" + date_and_time);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    fromWaiting();
                    return;
                }
                else{
                    announcementCompanyName = snapshot.child("announcement_company_name").getValue(String.class);
                    announcementDescription = snapshot.child("announcement_description").getValue(String.class);
                    announcementDuration = snapshot.child("announcement_duration").getValue(Date.class);
                    announcementAdditional = snapshot.child("announcement_additional").getValue(String.class);
                    announcementRestrictions = snapshot.child("age_restricted").getValue(Boolean.class);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(announcementDuration);
                int month = calendar.get(Calendar.MONTH) + 1;
                ends.setText(ends.getText().toString() + " " + announcementDuration.getHours() + ":" + announcementDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                //ends.setText(ends.getText().toString() + " " + announcementDuration.getTime());
                company.setText(company.getText().toString() + " "+ announcementCompanyName);
                desc.setText(desc.getText().toString() + " " + announcementDescription);
                additional.setText(additional.getText().toString() + " " + announcementAdditional);
                activityStatus.setText(activityStatus.getText() + ": " + active.getText().toString());

                if(announcementRestrictions){
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
        isFromAnnouncement = false;
        reference = database.getReference("WaitingSocialAnnouncements/" + date_and_time);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcementCompanyName = snapshot.child("CompanyName").getValue(String.class);
                announcementDescription = snapshot.child("announcement_description").getValue(String.class);
                announcementDuration = snapshot.child("announcement_duration").getValue(Date.class);
                announcementAdditional = snapshot.child("announcement_additional").getValue(String.class);
                announcementRestrictions = snapshot.child("age_restricted").getValue(Boolean.class);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(announcementDuration);
                int month = calendar.get(Calendar.MONTH) + 1;
                ends.setText(ends.getText().toString() + " " + announcementDuration.getHours() + ":" + announcementDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                //ends.setText(ends.getText().toString() + " " + announcementDuration.getTime());
                company.setText(company.getText().toString() + " "+ announcementCompanyName);
                desc.setText(desc.getText().toString() + " " + announcementDescription);
                additional.setText(additional.getText().toString() + " " + announcementAdditional);
                activityStatus.setText(activityStatus.getText() + ": " + during_the_verification.getText().toString());

                if(announcementRestrictions){
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
        if(isFromAnnouncement){
            reference = database.getReference("SocialAnnouncement/" + country + "/" + date_and_time);

        }
        else{
            reference = database.getReference("WaitingSocialAnnouncements/" + date_and_time);

        }
        reference.removeValue();
        reference = database.getReference("CompanyEmails/" + email_date_and_time + "/CompanySocialAnnouncement");
        reference.removeValue();

        Toast.makeText(getApplicationContext(), deleted.getText().toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(ManageSocialAnnouncement.this, Manage.class));
    }


    public void exit(View view){
        startActivity(new Intent(ManageSocialAnnouncement.this, Manage.class));
    }
}