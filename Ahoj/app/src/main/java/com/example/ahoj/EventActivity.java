package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Intent intent = getIntent();
        TextView event_name = (TextView) findViewById(R.id.activity_event_name);
        TextView event_ends_in = (TextView) findViewById(R.id.activityEventEventEndsAt);
        TextView event_location = (TextView) findViewById(R.id.activityEventEventLocation);
        TextView event_company = (TextView) findViewById(R.id.activityEventEventCompanyName);
        TextView event_desc = (TextView) findViewById(R.id.activityEventEventDescription);
        TextView event_additional = (TextView) findViewById(R.id.activityEventEventAdditional);

        String additional = getIntent().getStringExtra("Additional");

        event_name.setText(getIntent().getStringExtra("Name"));
        event_ends_in.setText(event_ends_in.getText() + " " + getIntent().getStringExtra("Duration"));
        event_location.setText(event_location.getText() + " " + getIntent().getStringExtra("Localization"));
        event_company.setText(event_company.getText() + " " + getIntent().getStringExtra("Company"));
        event_desc.setText(event_desc.getText() + " " + getIntent().getStringExtra("Description"));
        if(!additional.trim().isEmpty() ){
            event_additional.setText(event_additional.getText() + " " + additional);
        }
        else{
            event_additional.setVisibility(View.INVISIBLE);
        }

    }


    public void exitSettings (View view){
        startActivity(new Intent(EventActivity.this, MapActivityMain.class));
    }
}