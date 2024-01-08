package com.chatoyment.ahoyapp;
import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.ahoyapp.OnlyJava.OnlineDate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;

public class Manage extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String savedEmail = "", modifiedEmail = "", country = "", date_and_time = "";
    Date eventDuration, date;

    TextView you_dont_have_any_announcement, you_dont_have_any_event, you_dont_have_any_competition;

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
        setContentView(R.layout.activity_manage);

        OnlineDate.fetchDateAsync();

        date = OnlineDate.getDate();

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        savedEmail = sharedPreferences.getString("email", "");

        modifiedEmail = savedEmail.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");

        you_dont_have_any_announcement = findViewById(R.id.you_dont_have_any_announcement);
        you_dont_have_any_event = findViewById(R.id.you_dont_have_any_event);
        you_dont_have_any_competition = findViewById(R.id.you_dont_have_any_competition);
    }

    public void exit(View view){
        Intent intent = new Intent(Manage.this, MapActivityMain.class);
        intent.putExtra("activity", "main");
        startActivity(intent);

    }

    public void socialEvent(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail + "/CompanySocialEvent");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    date_and_time = snapshot.child("date_and_time").getValue(String.class);
                    eventDuration = snapshot.child("duration").getValue(Date.class);
                    country = snapshot.child("country").getValue(String.class);

                    if (date.after(eventDuration)){
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("SocialEvent/" + country).child(date_and_time);
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("WaitingSocialEvents/" + date_and_time);
                        reference.removeValue();

                        Toast.makeText(getApplicationContext(), you_dont_have_any_event.getText().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Manage.this, ManageSocialEvent.class);
                    intent.putExtra("date_and_time", date_and_time);
                    intent.putExtra("country", country);
                    intent.putExtra("email", modifiedEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), you_dont_have_any_event.getText().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void event(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail + "/CompanyEvent");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    date_and_time = snapshot.child("date_and_time").getValue(String.class);
                    eventDuration = snapshot.child("duration").getValue(Date.class);
                    country = snapshot.child("country").getValue(String.class);

                    if (date.after(eventDuration)){
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("Event/" + country).child(date_and_time);
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("Waiting/" + date_and_time);
                        reference.removeValue();

                        Toast.makeText(getApplicationContext(), you_dont_have_any_event.getText().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Manage.this, ManageEvent.class);
                    intent.putExtra("date_and_time", date_and_time);
                    intent.putExtra("country", country);
                    intent.putExtra("email", modifiedEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), you_dont_have_any_event.getText().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void socialAnnouncement(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail + "/CompanySocialAnnouncement");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    date_and_time = snapshot.child("date_and_time").getValue(String.class);
                    eventDuration = snapshot.child("duration").getValue(Date.class);
                    country = snapshot.child("country").getValue(String.class);

                    if (date.after(eventDuration)){
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("SocialAnnouncement/" + country).child(date_and_time);
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("WaitingSocialAnnouncements/" + date_and_time);
                        reference.removeValue();

                        Toast.makeText(getApplicationContext(), you_dont_have_any_announcement.getText().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Manage.this, ManageSocialAnnouncement.class);
                    intent.putExtra("date_and_time", date_and_time);
                    intent.putExtra("country", country);
                    intent.putExtra("email", modifiedEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), you_dont_have_any_announcement.getText().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void announcement(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail + "/CompanyAnnouncement");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    date_and_time = snapshot.child("date_and_time").getValue(String.class);
                    eventDuration = snapshot.child("duration").getValue(Date.class);
                    country = snapshot.child("country").getValue(String.class);

                    if (date.after(eventDuration)){
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("Announcement/" + country).child(date_and_time);
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("WaitingAnnouncements/" + date_and_time);
                        reference.removeValue();

                        Toast.makeText(getApplicationContext(), you_dont_have_any_announcement.getText().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Manage.this, ManageAnnouncement.class);
                    intent.putExtra("date_and_time", date_and_time);
                    intent.putExtra("country", country);
                    intent.putExtra("email", modifiedEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), you_dont_have_any_announcement.getText().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void competition(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail + "/CompanyCompetition");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    date_and_time = snapshot.child("date_and_time").getValue(String.class);
                    eventDuration = snapshot.child("duration").getValue(Date.class);
                    country = snapshot.child("country").getValue(String.class);

                    if (date.after(eventDuration)){
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("Announcement/" + country).child(date_and_time);
                        reference.removeValue();

                        reference = FirebaseDatabase.getInstance().getReference("WaitingAnnouncements/" + date_and_time);
                        reference.removeValue();

                        Toast.makeText(getApplicationContext(), you_dont_have_any_competition.getText().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Manage.this, ManageCompetition.class);
                    intent.putExtra("date_and_time", date_and_time);
                    intent.putExtra("country", country);
                    intent.putExtra("email", modifiedEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), you_dont_have_any_competition.getText().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}