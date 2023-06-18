package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EventLocalVirtualAnnouncement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_local_virtual_announcement);
    }

    public void exit(View view){
        startActivity(new Intent(EventLocalVirtualAnnouncement.this, MapActivityMain.class));
    }

    public void local(View view){
        startActivity(new Intent(EventLocalVirtualAnnouncement.this, AddLocalEvent.class));
    }
    public void announcement(View view){
        startActivity(new Intent(EventLocalVirtualAnnouncement.this, AddAnnouncement.class));
    }
}