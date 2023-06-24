package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AhoyAnnouncements extends AppCompatActivity {

    Intent intent;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("AhoyAnnouncements");

    String announcement;

    TextView ahoy_announcements;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahoy_announcements);

        intent = getIntent();
        ahoy_announcements = findViewById(R.id.textviewAhoyAnnouncement);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    announcement = snapshot.getValue(String.class);
                    ahoy_announcements.setText(announcement);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void exit(View view){
        if(intent.getStringExtra("activity").equals("user")){
            Intent intent_activity = new Intent(AhoyAnnouncements.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(AhoyAnnouncements.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
    }
}