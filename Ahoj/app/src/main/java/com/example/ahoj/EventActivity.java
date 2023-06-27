package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class EventActivity extends AppCompatActivity {

    AdView adview;

    String date_and_time = "", country = "", eventDescription, eventCompanyName, additional;
    TextView event_company, event_desc, event_additional;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Intent intent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        intent = getIntent();

        TextView event_name = (TextView) findViewById(R.id.activity_event_name);
        TextView event_ends_in = (TextView) findViewById(R.id.activityEventEventEndsAt);
        TextView event_location = (TextView) findViewById(R.id.activityEventEventLocation);
        event_company = (TextView) findViewById(R.id.activityEventEventCompanyName);
        event_desc = (TextView) findViewById(R.id.activityEventEventDescription);
        event_additional = (TextView) findViewById(R.id.activityEventEventAdditional);


        date_and_time = getIntent().getStringExtra("DateAndTime");
        country = getIntent().getStringExtra("Country");

        event_name.setText(getIntent().getStringExtra("Name"));
        event_ends_in.setText(event_ends_in.getText() + " " + getIntent().getStringExtra("Duration"));
        event_location.setText(event_location.getText() + " " + getIntent().getStringExtra("Localization"));



        reference = database.getReference("Event/" + country + "/" + date_and_time);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventDescription = snapshot.child("event_description").getValue(String.class);
                eventCompanyName = snapshot.child("event_company_name").getValue(String.class);
                additional = snapshot.child("event_additional").getValue(String.class);

                load();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });


        adview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);


    }

    void load(){
        event_company.setText(event_company.getText() + " " + eventCompanyName);
        event_desc.setText(event_desc.getText() +  " " + eventDescription);

        if(!additional.trim().isEmpty() ){
            event_additional.setText(event_additional.getText() + " " + additional);
        }
        else{
            event_additional.setVisibility(View.GONE);
        }
    }

    public void report(View view){
        //todo
    }


    public void exitSettings (View view){
        if(intent.getStringExtra("activity").equals("user")){
            Intent intent1 = new Intent(EventActivity.this, MapActivityMain.class);
            intent1.putExtra("activity", "user");
            startActivity(intent1);
        }
        else{
            Intent intent1 = new Intent(EventActivity.this, MapActivityMain.class);
            intent1.putExtra("activity", "main");
            startActivity(intent1);
        }
    }
}