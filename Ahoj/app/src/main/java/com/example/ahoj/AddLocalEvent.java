package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class AddLocalEvent extends AppCompatActivity {

    int page = 1;
    String nameV, descV, locationV, company_nameV, additionalV, durationStr, isSocial = "false";
    int durationV = 0;

    TextView must_have_name, must_have_desc, must_have_company_name, must_have_localization, must_last_hour;
    EditText event_name, event_desc, event_location, event_company_name, event_duration, event_additional, editName, description, location, company_name, duration, additional;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = sharedPreferences2.getString("selectedLanguage", null);

        if (savedLanguage.equals("en")) {
            Locale locale2 = new Locale("en");
            Locale.setDefault(locale2);
            Configuration config = new Configuration();
            config.locale = locale2;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            Locale myLocale = new Locale("en");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
        else if (savedLanguage.equals("pl")) {
            Locale locale2 = new Locale("pl");
            Locale.setDefault(locale2);
            Configuration config = new Configuration();
            config.locale = locale2;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            Locale myLocale = new Locale("pl");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        intent = getIntent();
        isSocial = intent.getStringExtra("isSocial");

        must_have_name = findViewById(R.id.textViewMustHaveName);
        must_have_desc = findViewById(R.id.textViewMustHaveDesc);
        must_have_company_name = findViewById(R.id.textViewMustHaveCompanyName);
        must_have_localization = findViewById(R.id.textViewMustHaveLocalization);
        must_last_hour = findViewById(R.id.textViewMustLastAHour);


        event_name = findViewById(R.id.event_name);
        event_desc = findViewById(R.id.event_description);
        event_location = findViewById(R.id.event_location);
        event_company_name = findViewById(R.id.event_company_name);
        event_duration = findViewById(R.id.event_duration);
        event_additional = findViewById(R.id.event_additional_info);


        editName = (EditText) findViewById(R.id.event_name);
        description = (EditText) findViewById(R.id.event_description);
        location = (EditText) findViewById(R.id.event_location);
        company_name = (EditText) findViewById(R.id.event_company_name);
        duration = (EditText) findViewById(R.id.event_duration);
        additional = (EditText) findViewById(R.id.event_additional_info);

        Intent autoIntent = getIntent();

        String test_name = autoIntent.getStringExtra("event_name");
        if(test_name != null){
            event_name.setText(autoIntent.getStringExtra("event_name"));
            event_desc.setText(autoIntent.getStringExtra("event_desc"));
            event_location.setText(autoIntent.getStringExtra("localization"));
            event_company_name.setText(autoIntent.getStringExtra("company_name"));
            event_duration.setText(autoIntent.getStringExtra("duration"));
            event_additional.setText(autoIntent.getStringExtra("additional"));
        }


    }

    public void exitAdd(View view) {
        startActivity(new Intent(AddLocalEvent.this, EventLocalVirtualAnnouncement.class));
    }

    public void addEvent(View view) {
        boolean canBeAdded = true;




        nameV = editName.getText().toString();
        descV = description.getText().toString();
        locationV = location.getText().toString();
        company_nameV = company_name.getText().toString();
        additionalV = additional.getText().toString();
        durationStr = duration.getText().toString();


        if (nameV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, must_have_name.getText().toString(), Toast.LENGTH_LONG).show();
        }
        if (descV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, must_have_desc.getText().toString(), Toast.LENGTH_LONG).show();
        }
        if (locationV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, must_have_localization.getText().toString(), Toast.LENGTH_LONG).show();
        }
        if (company_nameV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, must_have_company_name.getText().toString(), Toast.LENGTH_LONG).show();
        }
        if (durationStr.isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, must_last_hour.getText().toString(), Toast.LENGTH_LONG).show();
        }

        if (canBeAdded) {
            Intent intent = new Intent(AddLocalEvent.this, EventLocalizationPreview.class);

            intent.putExtra("event_name", nameV);
            intent.putExtra("event_desc", descV);
            intent.putExtra("localization", locationV);
            intent.putExtra("company_name", company_nameV);
            intent.putExtra("duration", durationStr);
            intent.putExtra("additional", additionalV);

            if(isSocial.equals("true")){
                intent.putExtra("isSocial", "true");
            }
            else{
                intent.putExtra("isSocial", "false");
            }


            startActivity(intent);

        }

    }

    public void statute(View view){
        Intent intent = new Intent(AddLocalEvent.this, Statute.class);

        intent.putExtra("activity", "AddLocalEvent");
        intent.putExtra("event_name", editName.getText().toString());
        intent.putExtra("event_desc", description.getText().toString());
        intent.putExtra("localization", location.getText().toString());
        intent.putExtra("company_name", company_name.getText().toString());
        intent.putExtra("duration", duration.getText().toString());
        intent.putExtra("additional", additional.getText().toString());

        if(isSocial.equals("true")){
            intent.putExtra("isSocial", "true");
        }
        else{
            intent.putExtra("isSocial", "false");
        }

        startActivity(intent);
    }
}