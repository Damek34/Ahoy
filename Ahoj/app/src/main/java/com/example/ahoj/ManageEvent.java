package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class ManageEvent extends AppCompatActivity {

    TextView name, ends, location, company, desc, additional, countryTextView, deleted;
    String date_and_time = "", country = "", eventDescription = "", eventCompanyName = "", eventLocation = "", eventAdditional = "", eventName = "", email= "";
    Date eventDuration;

    Intent intent;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Boolean isFromEvent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        intent = getIntent();


        name = findViewById(R.id.activity_event_name);
        ends = findViewById(R.id.activityEventEventEndsAt);
        location = findViewById(R.id.activityEventEventLocation);
        company = findViewById(R.id.activityEventEventCompanyName);
        desc = findViewById(R.id.activityEventEventDescription);
        additional = findViewById(R.id.activityEventEventAdditional);
        countryTextView = findViewById(R.id.activityEventEventCountry);
        deleted = findViewById(R.id.deleted);

        date_and_time = intent.getStringExtra("date_and_time");
        country = intent.getStringExtra("country");
        email = intent.getStringExtra("email");
        countryTextView.setText(countryTextView.getText().toString() + ": " + country);

        reference = database.getReference("Event/" + country + "/" + date_and_time);
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
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(eventDuration);
                int month = calendar.get(Calendar.MONTH) + 1;
                ends.setText(ends.getText().toString() + " " + eventDuration.getHours() + ":" + eventDuration.getMinutes() + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                name.setText(name.getText().toString() + " "+ eventName);
                location.setText(location.getText().toString() + " " +eventLocation);
                company.setText(company.getText().toString() + " " + eventCompanyName);
                desc.setText(desc.getText().toString() + " " + eventDescription);
                additional.setText(additional.getText().toString() + " " + eventAdditional);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void fromWaiting(){
        isFromEvent = false;
        reference = database.getReference("Waiting/" + date_and_time);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventName = snapshot.child("event_name").getValue(String.class);
                    eventDescription = snapshot.child("event_description").getValue(String.class);
                    eventCompanyName = snapshot.child("event_company_name").getValue(String.class);
                    eventLocation = snapshot.child("event_localization").getValue(String.class);
                    eventDuration = snapshot.child("event_duration").getValue(Date.class);
                    eventAdditional = snapshot.child("event_additional").getValue(String.class);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(eventDuration);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    ends.setText(ends.getText().toString() + " " + eventDuration.getHours() + ":" + eventDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                    name.setText(name.getText().toString() + " "+ eventName);
                    location.setText(location.getText().toString() + " " +eventLocation);
                    company.setText(company.getText().toString() + " " + eventCompanyName);
                    desc.setText(desc.getText().toString() + " " + eventDescription);
                    additional.setText(additional.getText().toString() + " " + eventAdditional);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(View view){
        if(isFromEvent){
            reference = database.getReference("Event/" + country + "/" + date_and_time);
            reference.removeValue();

            reference = database.getReference("CompanyEmails/" + email + "/CompanyEvent");
            reference.removeValue();
        }
        else{
            reference = database.getReference("Waiting/" + date_and_time);
            reference.removeValue();

            reference = database.getReference("CompanyEmails/" + email + "/CompanyEvent");
            reference.removeValue();
        }

        Toast.makeText(getApplicationContext(), deleted.getText().toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(ManageEvent.this, Manage.class));
    }

    public void exit(View view){
        startActivity(new Intent(ManageEvent.this, Manage.class));
    }
}