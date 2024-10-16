package com.chatoyment.ahoyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.OnlyJava.ForbiddenWords;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddLocalEvent extends AppCompatActivity implements OnlineDate.OnDateFetchedListener {

    int page = 1;
    String nameV, descV, locationV, company_nameV, additionalV, durationStr, isSocial = "false", intent_restricted, available_to_everyone;
    int durationV = 0;

    TextView must_have_name, must_have_desc, must_have_company_name, must_have_localization, must_last_hour, duration_preview, error_caused_by_unstable_internet_connection
            , age_restricted, checkbox_adult_info, application_contains_forbidden_expression;
    EditText event_name, event_desc, event_location, event_company_name, event_duration, event_additional, editName, description, location, company_name, duration, additional;

    Intent intent;

    Date date;
    CheckBox checkbox_adult;

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
        duration_preview = findViewById(R.id.duration_preview);
        error_caused_by_unstable_internet_connection = findViewById(R.id.error_caused_by_unstable_internet_connection);
        checkbox_adult = findViewById(R.id.checkbox_adult);
        age_restricted = findViewById(R.id.age_restricted);
        checkbox_adult_info = findViewById(R.id.checkbox_adult_info);
        application_contains_forbidden_expression = findViewById(R.id.application_contains_forbidden_expression);

        available_to_everyone = checkbox_adult_info.getText().toString();

        Date date = OnlineDate.getDate();

        Intent activity_intent = getIntent();

        String test_name = activity_intent.getStringExtra("event_name");
        if(test_name != null){
            event_name.setText(activity_intent.getStringExtra("event_name"));
            event_desc.setText(activity_intent.getStringExtra("event_desc"));
            event_location.setText(activity_intent.getStringExtra("localization"));
            event_company_name.setText(activity_intent.getStringExtra("company_name"));
            event_duration.setText(activity_intent.getStringExtra("duration"));
            event_additional.setText(activity_intent.getStringExtra("additional"));

            intent_restricted = activity_intent.getStringExtra("restricted");
            if(intent_restricted.equals("true")){
                checkbox_adult.setChecked(true);
                checkbox_adult.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.red)));
                checkbox_adult_info.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                checkbox_adult_info.setText(age_restricted.getText().toString());
            }
            else{
                checkbox_adult.setChecked(false);
                checkbox_adult.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.green)));
                checkbox_adult_info.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                checkbox_adult_info.setText(available_to_everyone);
            }
        }

        event_duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!event_duration.getText().toString().isEmpty()){
                    char[] duration_char = event_duration.getText().toString().toCharArray();
                    if(String.valueOf(duration_char[0]).equals("0")){
                        duration_preview.setText("");
                        event_duration.setText("");
                        return;
                    }
                }

                if(event_duration.getText().toString().trim().equals("")){
                    duration_preview.setText("");
                }
                else{
               //     Date date = OnlineDate.getDate();
                    long millis = System.currentTimeMillis();
                    String date_and_time = date + " " + millis;


                    Calendar calendar = Calendar.getInstance();
                    try{
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR, Integer.parseInt(event_duration.getText().toString()));
                    }
                    catch(NullPointerException e){
                        duration_preview.setText(error_caused_by_unstable_internet_connection.getText().toString());
                        fetchDate();
                    }

                    duration_preview.setText(calendar.getTime().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        checkbox_adult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkbox_adult.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.red)));
                    checkbox_adult_info.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    checkbox_adult_info.setText(age_restricted.getText().toString());
                }
                else{
                    checkbox_adult.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.green)));
                    checkbox_adult_info.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                    checkbox_adult_info.setText(available_to_everyone);
                }
            }
        });

    }

    public void exitAdd(View view) {
        startActivity(new Intent(AddLocalEvent.this, SelectWhatToAdd.class));
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


        boolean have_forbidden_words = ForbiddenWords.containsForbiddenWord(editName);
        if(!have_forbidden_words){
            have_forbidden_words = ForbiddenWords.containsForbiddenWord(description);
            if(!have_forbidden_words){
                have_forbidden_words = ForbiddenWords.containsForbiddenWord(company_name);
                if(!have_forbidden_words){
                    have_forbidden_words = ForbiddenWords.containsForbiddenWord(additional);
                    if(!have_forbidden_words){
                        if (canBeAdded) {
                            Intent intent = new Intent(AddLocalEvent.this, EventLocalizationPreview.class);

                            intent.putExtra("event_name", nameV);
                            intent.putExtra("event_desc", descV);
                            intent.putExtra("localization", locationV);
                            intent.putExtra("company_name", company_nameV);
                            intent.putExtra("duration", durationStr);
                            intent.putExtra("additional", additionalV);
                            intent.putExtra("restricted", String.valueOf(checkbox_adult.isChecked()));

                            if(isSocial.equals("true")){
                                intent.putExtra("isSocial", "true");
                            }
                            else{
                                intent.putExtra("isSocial", "false");
                            }


                            startActivity(intent);

                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), application_contains_forbidden_expression.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), application_contains_forbidden_expression.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), application_contains_forbidden_expression.getText().toString(), Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), application_contains_forbidden_expression.getText().toString(), Toast.LENGTH_LONG).show();
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
        intent.putExtra("restricted", String.valueOf(checkbox_adult.isChecked()));

        if(isSocial.equals("true")){
            intent.putExtra("isSocial", "true");
        }
        else{
            intent.putExtra("isSocial", "false");
        }

        startActivity(intent);
    }

    public void fetchDate() {
        OnlineDate.fetchDateAsync(this);
    }

    @Override
    public void onDateFetched(Date date) {

    }
}