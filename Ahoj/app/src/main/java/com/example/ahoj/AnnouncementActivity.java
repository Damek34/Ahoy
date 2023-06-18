package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class AnnouncementActivity extends AppCompatActivity {

    Intent intent;
    String user_or_main;
    String data_and_time, company_name, country;

    TextView company, desc, duration, additional;
    String descStr, additionalStr, descCopy, durationCopy, additionalCopy;
    Date durationDate;
    AdView adView;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        intent = getIntent();

        data_and_time = intent.getStringExtra("id");
        company_name = intent.getStringExtra("company");
        user_or_main = intent.getStringExtra("activity");
        country = intent.getStringExtra("country");

        company = findViewById(R.id.activity_announcement_company_name);
        desc = findViewById(R.id.activityAnnouncementDescription);
        duration = findViewById(R.id.activityAnnouncementEndsAt);
        additional = findViewById(R.id.activityAnnouncementAdditional);

        descCopy = desc.getText().toString();
        durationCopy = duration.getText().toString();
        additionalCopy = additional.getText().toString();



        company.setText(company_name);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });


        adView = findViewById(R.id.adViewAnnouncement);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        reference = database.getReference("Announcement/" + country + "/" + data_and_time);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    descStr = snapshot.child("announcement_description").getValue(String.class);
                    durationDate = snapshot.child("announcement_duration").getValue(Date.class);
                    additionalStr = snapshot.child("announcement_additional").getValue(String.class);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(durationDate);
                    int month = calendar.get(Calendar.MONTH) + 1;

                    desc.setText(descCopy + " " + descStr);
                    additional.setText(additionalCopy + " " + additionalStr);
                    duration.setText(durationCopy + " " +  durationDate.getHours() + ":" + durationDate.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));


                    if(additionalStr.trim().isEmpty()){
                        additional.setVisibility(View.GONE);
                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }

    public void exitAnnouncement(View view){
        if(user_or_main.equals("user")){
            startActivity(new Intent(AnnouncementActivity.this, MapForUser.class));
        }
        else{
            startActivity(new Intent(AnnouncementActivity.this, MapActivityMain.class));

        }
    }
}