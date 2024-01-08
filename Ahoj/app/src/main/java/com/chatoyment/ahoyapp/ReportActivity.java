package com.chatoyment.ahoyapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.chatoyment.ahoyapp.R;
import com.example.ahoyapp.OnlyJava.ReportInfo;
import com.example.ahoyapp.OnlyJava.SendEventReportToAhoyModeratorsInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    String date_and_time = "", country = "", organizer = "", nick = "", social_mode, isavailable;
    Intent activity_intent;

    EditText reason;
    TextView your_report_should_have_twenty_letters, you_already_reported_that_event, Success;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    int count = 0;
    Boolean hasReported = false;

    List<String> users = new ArrayList<>();


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
        setContentView(R.layout.activity_report);

        activity_intent = getIntent();
        if(activity_intent.getStringExtra("announcement_or_event").equals("announcement")){
            date_and_time = activity_intent.getStringExtra("date_and_time");
        }
        else{
            date_and_time = activity_intent.getStringExtra("DateAndTime");
        }

        country = activity_intent.getStringExtra("country");
        organizer = activity_intent.getStringExtra("organizer");
        social_mode = activity_intent.getStringExtra("social_mode");
        isavailable = activity_intent.getStringExtra("isavailable");

        reason = findViewById(R.id.report_event_reason);
        your_report_should_have_twenty_letters = findViewById(R.id.your_report_should_have_twenty_letters);
        you_already_reported_that_event = findViewById(R.id.you_already_reported_that_event);
        Success = findViewById(R.id.Success);

        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferences.getString("nick", "");
    }

    public void report(View view){
        if(reason.getText().toString().trim().length() < 20 ){
            Toast.makeText(getApplicationContext(), your_report_should_have_twenty_letters.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(activity_intent.getStringExtra("announcement_or_event").equals("announcement")){
            if (social_mode.equals("false")) {
                reference = database.getReference("Announcement/" + country + "/" + date_and_time);
            } else {
                reference = database.getReference("SocialAnnouncement/" + country + "/" + date_and_time);
            }
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int i = 0;
                        for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                            if (snapshot.child("Reported/" + nick).exists()) {
                                hasReported = true;
                                Toast.makeText(getApplicationContext(), you_already_reported_that_event.getText().toString(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        if (!hasReported) {
                            ReportInfo reportInfo = new ReportInfo(nick, reason.getText().toString());
                            reference.child("Reported/" + nick).setValue(reportInfo);

                            count = snapshot.child("times_reported").getValue(Integer.class);
                            count++;
                            reference.child("times_reported").setValue(count);

                            Toast.makeText(getApplicationContext(), Success.getText().toString(), Toast.LENGTH_LONG).show();

                            if (count >= 5) {
                                sendToAhoyModerators();
                            }
                            View exit = findViewById(R.id.buttonReport);
                            exit(exit);
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else {
            if (social_mode.equals("false")) {
                reference = database.getReference("Event/" + country + "/" + date_and_time);
            } else {
                reference = database.getReference("SocialEvent/" + country + "/" + date_and_time);
            }

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int i = 0;
                        for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                            if (snapshot.child("Reported/" + nick).exists()) {
                                hasReported = true;
                                Toast.makeText(getApplicationContext(), you_already_reported_that_event.getText().toString(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        if (!hasReported) {
                            ReportInfo reportInfo = new ReportInfo(nick, reason.getText().toString());
                            reference.child("Reported/" + nick).setValue(reportInfo);

                            count = snapshot.child("times_reported").getValue(Integer.class);
                            count++;
                            reference.child("times_reported").setValue(count);

                            Toast.makeText(getApplicationContext(), Success.getText().toString(), Toast.LENGTH_LONG).show();

                            if (count >= 5) {
                                sendToAhoyModerators();
                            }
                            View exit = findViewById(R.id.buttonReport);
                            exit(exit);
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    void sendToAhoyModerators(){
        if(activity_intent.getStringExtra("announcement_or_event").equals("announcement")){
            reference = database.getReference("ReportedAnnouncements");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isSocial = false;
                    if(social_mode.equals("true")){
                        isSocial = true;
                    }
                    SendEventReportToAhoyModeratorsInfo report = new SendEventReportToAhoyModeratorsInfo(date_and_time, isSocial, country);
                    reference.child(date_and_time).setValue(report);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            reference = database.getReference("ReportedEvents");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isSocial = false;
                    if(social_mode.equals("true")){
                        isSocial = true;
                    }
                    SendEventReportToAhoyModeratorsInfo report = new SendEventReportToAhoyModeratorsInfo(date_and_time, isSocial, country);
                    reference.child(date_and_time).setValue(report);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


    public void exit(View view){
        if(activity_intent.getStringExtra("announcement_or_event").equals("announcement")){
            if (activity_intent.getStringExtra("activity").equals("user")){
                Intent intent1 = new Intent(ReportActivity.this, AnnouncementActivity.class);
                intent1.putExtra("id", date_and_time);
                intent1.putExtra("company", activity_intent.getStringExtra("company"));
                intent1.putExtra("activity", "user");
                intent1.putExtra("country", country);
                intent1.putExtra("announcement_or_event", "announcement");

                if(social_mode.equals("false")){
                    intent1.putExtra("isSocial", "false");
                }
                else{
                    intent1.putExtra("isSocial", "true");
                }

                startActivity(intent1);
            }
            else{
                Intent intent1 = new Intent(ReportActivity.this, AnnouncementActivity.class);
                intent1.putExtra("id", date_and_time);
                intent1.putExtra("company", activity_intent.getStringExtra("company"));
                intent1.putExtra("activity", "main");
                intent1.putExtra("country", country);
                intent1.putExtra("announcement_or_event", "announcement");

                if(social_mode.equals("false")){
                    intent1.putExtra("isSocial", "false");
                }
                else{
                    intent1.putExtra("isSocial", "true");
                }

                startActivity(intent1);
            }

        }
        else {
            if (activity_intent.getStringExtra("activity").equals("user")) {
                Intent intent1 = new Intent(ReportActivity.this, EventActivity.class);
                intent1.putExtra("activity", "user");
                intent1.putExtra("DateAndTime", date_and_time);
                intent1.putExtra("Country", country);
                intent1.putExtra("organizer", organizer);
                intent1.putExtra("social_mode", social_mode);
                intent1.putExtra("isavailable", isavailable);
                intent1.putExtra("Localization", activity_intent.getStringExtra("Localization"));
                intent1.putExtra("Duration", activity_intent.getStringExtra("Duration"));
                intent1.putExtra("Name", activity_intent.getStringExtra("Name"));
                startActivity(intent1);
            } else {
                Intent intent1 = new Intent(ReportActivity.this, EventActivity.class);
                intent1.putExtra("activity", "main");
                intent1.putExtra("DateAndTime", date_and_time);
                intent1.putExtra("country", country);
                intent1.putExtra("organizer", organizer);
                intent1.putExtra("social_mode", social_mode);
                intent1.putExtra("isavailable", isavailable);
                intent1.putExtra("Localization", activity_intent.getStringExtra("Localization"));
                intent1.putExtra("Duration", activity_intent.getStringExtra("Duration"));
                intent1.putExtra("Name", activity_intent.getStringExtra("Name"));

                startActivity(intent1);
            }
        }
    }
}