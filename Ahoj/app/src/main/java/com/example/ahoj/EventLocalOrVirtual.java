package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EventLocalOrVirtual extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_local_or_virtual);
    }

    public void exit(View view){
        startActivity(new Intent(EventLocalOrVirtual.this, MapActivityMain.class));
    }

    public void local(View view){
        startActivity(new Intent(EventLocalOrVirtual.this, AddLocalEvent.class));
    }
}