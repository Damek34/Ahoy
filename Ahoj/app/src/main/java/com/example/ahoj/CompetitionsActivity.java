package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahoj.OnlyJava.OnlineDate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompetitionsActivity extends AppCompatActivity {

    Intent intent;
    Spinner country_spinner;
    String country = "";
    LinearLayout linear_layout_competitions;
    Button save;
    boolean is_filter_opened = false;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference, reference_remove;
    Date date;

    TextView no_competitions_found, check_internet_connection, no_competitions_found_notification;
    EditText search;
    ConstraintLayout main_layout;

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
        setContentView(R.layout.activity_competitions);

        intent = getIntent();

        country = intent.getStringExtra("country");

        country_spinner = findViewById(R.id.country_spinner);
        linear_layout_competitions  = findViewById(R.id.linear_layout_competitions);
        save = findViewById(R.id.save);
        no_competitions_found = findViewById(R.id.no_competitions_found);
        check_internet_connection = findViewById(R.id.check_internet_connection);
        search = findViewById(R.id.search);
        main_layout = findViewById(R.id.main_layout);

        date = OnlineDate.getDate();
        no_competitions_found_notification = new TextView(getApplicationContext());

        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();

        for(Locale locale: locales){
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);

        country_spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.custom_spinner);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!search.getText().toString().trim().equals("")){

                    no_competitions_found_notification.setText(no_competitions_found.getText().toString());
                    no_competitions_found_notification.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                    no_competitions_found_notification.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                    no_competitions_found_notification.setTextSize(18);
                    no_competitions_found_notification.setGravity(Gravity.CENTER_HORIZONTAL);

                    boolean isTextViewFound = false;

                    for (int i = 0; i < linear_layout_competitions.getChildCount(); i++) {
                        View childView = linear_layout_competitions.getChildAt(i);

                        if (childView instanceof TextView) {
                            TextView textView = (TextView) childView;

                            // Porównaj textView z no_competitions_found_notification
                            if (textView.getText().toString().equals(no_competitions_found_notification.getText().toString())) {
                                isTextViewFound = true;
                                break;
                            }
                        }
                    }
                    if (!isTextViewFound){
                        linear_layout_competitions.addView(no_competitions_found_notification);
                    }

                    searchButtons(s.toString());
                }
                else{
                    for (int i = 0; i < linear_layout_competitions.getChildCount(); i += 2) {
                        View view = linear_layout_competitions.getChildAt(i);
                        if (view instanceof Button) {
                            Button button = (Button) view;
                            //  String title = button.getText().toString();
                            button.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);

                        }
                    }
                    linear_layout_competitions.removeView(no_competitions_found_notification);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.clearFocus();
            }
        });


     //   Toast.makeText(getApplicationContext(), country, Toast.LENGTH_LONG).show();
        scan();
    }

    void searchButtons(String query) {
        boolean foundMatch = false;

        for (int i = 0; i < linear_layout_competitions.getChildCount(); i += 2) {
            View view = linear_layout_competitions.getChildAt(i);
            if (view instanceof Button) {
                Button button = (Button) view;
                String title = button.getText().toString();
                if (title.toLowerCase().contains(query.toLowerCase())) {
                    button.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    no_competitions_found_notification.setVisibility(View.GONE);
                    foundMatch = true;
                }

                else {
                    button.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
            }
        }
        if (!foundMatch) {
            no_competitions_found_notification.setVisibility(View.VISIBLE);
        }


    }

    void scan(){
        reference = database.getReference("Competitions/" + country);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Date competition_date_temp =  snapshot.child("duration").getValue(Date.class);

                        if(date.before(competition_date_temp)){
                            String title = snapshot.child("title").getValue(String.class);
                            String date_and_time = snapshot.child("date_and_time").getValue(String.class);

                            Button check_competition_btn = new Button(getApplicationContext());
                            check_competition_btn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));
                            check_competition_btn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                            check_competition_btn.setText(title);

                            View view = new View(getApplicationContext());
                            view.setPadding(0, 20, 0, 20);
                            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));


                            int topMarginInPixels = getResources().getDimensionPixelSize(R.dimen.ten_dp);

                            LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);

                            paramsButton.setMargins(0, topMarginInPixels, 0, topMarginInPixels);

                            check_competition_btn.setLayoutParams(paramsButton);

                            linear_layout_competitions.addView(check_competition_btn);

                            LinearLayout.LayoutParams paramsView = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    getResources().getDimensionPixelSize(R.dimen.one_dp));

                            view.setLayoutParams(paramsView);

                            linear_layout_competitions.addView(view);

                            check_competition_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(intent.getStringExtra("activity").equals("main")){
                                        Intent intent_activity = new Intent(CompetitionsActivity.this, Competition.class);
                                        intent_activity.putExtra("activity", "main");
                                        intent_activity.putExtra("date_and_time", date_and_time);
                                        intent_activity.putExtra("country", country);
                                        startActivity(intent_activity);
                                    }
                                    else{
                                        Intent intent_activity = new Intent(CompetitionsActivity.this, Competition.class);
                                        intent_activity.putExtra("activity", "user");
                                        intent_activity.putExtra("date_and_time", date_and_time);
                                        intent_activity.putExtra("country", country);
                                        startActivity(intent_activity);
                                    }
                                }
                            });

                        }
                        else{
                            String date_and_time = snapshot.child("date_and_time").getValue(String.class);
                            String organizer_email = snapshot.child("organizer_email").getValue(String.class);

                            String modifiedEmail = organizer_email.replace(".", ",");
                            modifiedEmail = modifiedEmail.replace("#", "_");
                            modifiedEmail = modifiedEmail.replace("$", "-");
                            modifiedEmail = modifiedEmail.replace("[", "(");
                            modifiedEmail = modifiedEmail.replace("]", ")");

                            reference_remove = database.getReference("Competitions/" + country +"/" + date_and_time);
                            reference_remove.removeValue();

                            reference_remove = database.getReference("CompanyEmails/" + modifiedEmail +"/CompanyCompetition");
                            reference_remove.removeValue();

                        }

                    }
                }
                else{
                    TextView no_competitions_found_notification = new TextView(getApplicationContext());
                    no_competitions_found_notification.setText(no_competitions_found.getText().toString());
                    no_competitions_found_notification.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
                    no_competitions_found_notification.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                    no_competitions_found_notification.setTextSize(18);
                    no_competitions_found_notification.setGravity(Gravity.CENTER_HORIZONTAL);

                    linear_layout_competitions.addView(no_competitions_found_notification);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void save(View view){

        linear_layout_competitions.removeAllViews();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(country_spinner.getSelectedItem().toString(), 1);
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(), check_internet_connection.getText().toString(), Toast.LENGTH_LONG).show();
        }

        if(addresses == null){
            return;
        }

        if (addresses.size() > 0) {
            country = addresses.get(0).getCountryName();
        }
        is_filter_opened = false;
        save.setVisibility(View.GONE);
        country_spinner.setVisibility(View.GONE);
        scan();
    }

    public void exit (View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(CompetitionsActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(CompetitionsActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }
    public void filter(View view){
        if(is_filter_opened){
            is_filter_opened = false;
            save.setVisibility(View.GONE);
            country_spinner.setVisibility(View.GONE);
        }
        else{
            is_filter_opened = true;
            save.setVisibility(View.VISIBLE);
            country_spinner.setVisibility(View.VISIBLE);
        }
    }
}