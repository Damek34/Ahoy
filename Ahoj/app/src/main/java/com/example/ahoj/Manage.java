package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Manage extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String savedEmail = "", modifiedEmail = "", country = "", date_and_time = "";
    Date eventDuration, date;

    TextView you_dont_have_any_announcement, you_dont_have_any_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        date = new Date();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        savedEmail = sharedPreferences.getString("email", "");

        modifiedEmail = savedEmail.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");

        you_dont_have_any_announcement = findViewById(R.id.you_dont_have_any_announcement);
        you_dont_have_any_event = findViewById(R.id.you_dont_have_any_event);
    }

    public void exit(View view){
        Intent intent = new Intent(Manage.this, MapActivityMain.class);
        intent.putExtra("activity", "main");
        startActivity(intent);

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

                        Toast.makeText(getApplicationContext(), you_dont_have_any_event.getText().toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(Manage.this, ManageEvent.class);
                    intent.putExtra("date_and_time", date_and_time);
                    intent.putExtra("country", country);
                    intent.putExtra("email", modifiedEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), you_dont_have_any_event.getText().toString(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(getApplicationContext(), you_dont_have_any_announcement.getText().toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(Manage.this, ManageAnnouncement.class);
                    intent.putExtra("date_and_time", date_and_time);
                    intent.putExtra("country", country);
                    intent.putExtra("email", modifiedEmail);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), you_dont_have_any_announcement.getText().toString(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}