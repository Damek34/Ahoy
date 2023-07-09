package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Statute extends AppCompatActivity {

    String nameV, descV, locationV, company_nameV, durationStr, additionalV, announcement_desc, announcement_company_name, announcement_duration, announcement_additional;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statute);
        intent = getIntent();
        if(intent.getStringExtra("activity").equals("AddLocalEvent")){
            nameV = intent.getStringExtra("event_name");
            descV = intent.getStringExtra("event_desc");
            locationV = intent.getStringExtra("localization");
            company_nameV = intent.getStringExtra("company_name");
            durationStr = intent.getStringExtra("duration");
            additionalV = intent.getStringExtra("additional");
        }

        if(intent.getStringExtra("activity").equals("AddAnnouncement")){

            announcement_desc = intent.getStringExtra("event_desc");
            announcement_company_name = intent.getStringExtra("company_name");
            announcement_duration = intent.getStringExtra("duration");
            announcement_additional = intent.getStringExtra("additional");

        }

    }

    public void exit(View view) {
        if(intent.getStringExtra("activity").equals("AddLocalEvent")){

        Intent intent2 = new Intent(Statute.this, AddLocalEvent.class);

        intent2.putExtra("event_name", nameV);
        intent2.putExtra("event_desc", descV);
        intent2.putExtra("localization", locationV);
        intent2.putExtra("company_name", company_nameV);
        intent2.putExtra("duration", durationStr);
        intent2.putExtra("additional", additionalV);

        startActivity(intent2);
        }

        if(intent.getStringExtra("activity").equals("AddAnnouncement")){
            Intent statue = new Intent(Statute.this, AddAnnouncement.class);

            statue.putExtra("event_desc", announcement_desc);
            statue.putExtra("company_name", announcement_company_name);
            statue.putExtra("duration", announcement_duration);
            statue.putExtra("additional", announcement_additional);

            startActivity(statue);
        }

        if(intent.getStringExtra("activity").equals("mainsettings")){
            Intent intent2 = new Intent(Statute.this, SettingActivity.class);
            intent2.putExtra("activity", "main");
            startActivity(intent2);
        }
        if(intent.getStringExtra("activity").equals("usersettings")){
            Intent intent2 = new Intent(Statute.this, SettingActivity.class);
            intent2.putExtra("activity", "user");
            startActivity(intent2);
        }
    }
}