package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Intent intent = getIntent();
        TextView event_name = (TextView) findViewById(R.id.activity_event_name);


        event_name.setText(getIntent().getStringExtra("Name"));

    }


    public void exitSettings (View view){
        startActivity(new Intent(EventActivity.this, MapActivityMain.class));
    }
}