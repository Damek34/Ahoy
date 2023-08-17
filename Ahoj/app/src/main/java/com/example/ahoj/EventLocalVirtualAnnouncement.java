package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class EventLocalVirtualAnnouncement extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String savedEmail = "", modifiedEmail = "";
    TextView you_already_created_announcement, you_already_created_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_local_virtual_announcement);

        you_already_created_event = findViewById(R.id.you_already_created_event);
        you_already_created_announcement = findViewById(R.id.you_already_created_announcement);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        savedEmail = sharedPreferences.getString("email", "");

        modifiedEmail = savedEmail.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");



    }

    public void exit(View view){
       Intent intent = new Intent(EventLocalVirtualAnnouncement.this, MapActivityMain.class);
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
                    startActivity(new Intent(EventLocalVirtualAnnouncement.this, AddLocalEvent.class));
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
                if(snapshot.child("CompanyEvent").exists()){
                    Toast.makeText(getApplicationContext(), you_already_created_announcement.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    startActivity(new Intent(EventLocalVirtualAnnouncement.this, AddAnnouncement.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}