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

import java.util.Locale;

public class SelectWhatToAdd extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String savedEmail = "", modifiedEmail = "";
    TextView you_already_created_announcement, you_already_created_event, you_already_created_competition;

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
        setContentView(R.layout.select_what_to_add);

        you_already_created_event = findViewById(R.id.you_already_created_event);
        you_already_created_announcement = findViewById(R.id.you_already_created_announcement);
        you_already_created_competition = findViewById(R.id.you_already_created_competition);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        savedEmail = sharedPreferences.getString("email", "");

        modifiedEmail = savedEmail.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");



    }

    public void exit(View view){
       Intent intent = new Intent(SelectWhatToAdd.this, MapActivityMain.class);
       intent.putExtra("activity", "main");
        startActivity(intent);

    }

    public void local(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("CompanyEvent").exists()){
                    Toast.makeText(getApplicationContext(), you_already_created_event.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Intent intent = new Intent(SelectWhatToAdd.this, AddLocalEvent.class);
                    intent.putExtra("isSocial", "false");
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void localSocial(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("CompanySocialEvent").exists()){
                    Toast.makeText(getApplicationContext(), you_already_created_event.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Intent intent = new Intent(SelectWhatToAdd.this, AddLocalEvent.class);
                    intent.putExtra("isSocial", "true");
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void socialAnnouncement(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("CompanySocialAnnouncement").exists()){
                    Toast.makeText(getApplicationContext(), you_already_created_announcement.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Intent intent = new Intent(SelectWhatToAdd.this, AddAnnouncement.class);
                    intent.putExtra("isSocial", "true");
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void announcement(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("CompanyAnnouncement").exists()){
                    Toast.makeText(getApplicationContext(), you_already_created_announcement.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Intent intent = new Intent(SelectWhatToAdd.this, AddAnnouncement.class);
                    intent.putExtra("isSocial", "false");
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void competition(View view){
        reference = database.getReference("CompanyEmails/" + modifiedEmail);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("CompanyCompetition").exists()){
                    Toast.makeText(getApplicationContext(), you_already_created_competition.getText().toString(), Toast.LENGTH_LONG).show();
                }
                else{
                    startActivity(new Intent(SelectWhatToAdd.this, AddCompetition.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}