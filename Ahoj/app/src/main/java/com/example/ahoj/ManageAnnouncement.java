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

public class ManageAnnouncement extends AppCompatActivity {

    TextView ends, company, desc, additional, countryTextView, deleted;
    String date_and_time = "", country = "", announcementDescription = "", announcementCompanyName = "", announcementAdditional = "", email= "";
    Date announcementDuration;

    Intent intent;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Boolean isFromAnnouncement = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_announcement);

        intent = getIntent();

        date_and_time = intent.getStringExtra("date_and_time");
        country = intent.getStringExtra("country");
        email = intent.getStringExtra("email");

        ends = findViewById(R.id.activityAnnouncementEndsAt);
        company = findViewById(R.id.activity_announcement_company_name);
        desc = findViewById(R.id.activityAnnouncementDescription);
        additional = findViewById(R.id.activityAnnouncementAdditional);
        countryTextView = findViewById(R.id.activityAnnouncementCountry);
        deleted = findViewById(R.id.deleted);


        countryTextView.setText(countryTextView.getText().toString() + ": " + country);



        reference = database.getReference("Announcement/" + country + "/" + date_and_time);
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
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(announcementDuration);
                int month = calendar.get(Calendar.MONTH) + 1;
                ends.setText(ends.getText().toString() + " " + announcementDuration.getHours() + ":" + announcementDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                company.setText(company.getText().toString() + " "+ announcementCompanyName);
                desc.setText(desc.getText().toString() + " " + announcementDescription);
                additional.setText(additional.getText().toString() + " " + announcementAdditional);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    void fromWaiting(){
        isFromAnnouncement = false;
        reference = database.getReference("WaitingAnnouncements/" + date_and_time);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                announcementCompanyName = snapshot.child("CompanyName").getValue(String.class);
                announcementDescription = snapshot.child("announcement_description").getValue(String.class);
                announcementDuration = snapshot.child("announcement_duration").getValue(Date.class);
                announcementAdditional = snapshot.child("announcement_additional").getValue(String.class);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(announcementDuration);
                int month = calendar.get(Calendar.MONTH) + 1;
                ends.setText(ends.getText().toString() + " " + announcementDuration.getHours() + ":" + announcementDuration.getMinutes() + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                company.setText(company.getText().toString() + " "+ announcementCompanyName);
                desc.setText(desc.getText().toString() + " " + announcementDescription);
                additional.setText(additional.getText().toString() + " " + announcementAdditional);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(View view){
        if(isFromAnnouncement){
            reference = database.getReference("Announcement/" + country + "/" + date_and_time);

        }
        else{
            reference = database.getReference("WaitingAnnouncements/" + date_and_time);

        }
        reference.removeValue();
        reference = database.getReference("CompanyEmails/" + email + "/CompanyAnnouncement");
        reference.removeValue();

        Toast.makeText(getApplicationContext(), deleted.getText().toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(ManageAnnouncement.this, Manage.class));
    }


    public void exit(View view){
        startActivity(new Intent(ManageAnnouncement.this, Manage.class));
    }
}