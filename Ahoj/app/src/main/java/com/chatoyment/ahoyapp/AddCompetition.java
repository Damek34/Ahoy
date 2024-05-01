package com.chatoyment.ahoyapp;

import android.animation.Animator;
import android.content.Context;
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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;
import com.example.ahoyapp.OnlyJava.AddCompetitionInfo;
import com.example.ahoyapp.OnlyJava.CompanyCompetition;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddCompetition extends AppCompatActivity implements OnlineDate.OnDateFetchedListener {

    EditText competition_title_editText, competition_organizer_editText, competition_reward, competition_description, competition_duration_editText, competition_when_results_editText
            , competition_who_can_take_part_editText, competition_where_results_editText, competition_additional_info;

    Spinner competitionCountry;
    TextView duration_preview, competition_must_have_title, competition_must_have_organizer, competition_must_have_reward, competition_must_have_description
            , competition_must_last_at_least_an_hour, competition_must_have_information_when_the_results_will_be_available, competition_must_have_information_who_can_take_part_in_competition
            , competition_must_have_information_where_will_be_results_announced, check_internet_connection, success, error_caused_by_unstable_internet_connection
            , your_application_is_being_reviewed;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Date date;

    LottieAnimationView done_animation;
    Button ok;
    ScrollView scrollview;

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
        setContentView(R.layout.activity_add_competition);
        Date date = OnlineDate.getDate();

        competition_title_editText = findViewById(R.id.competition_title_editText);
        competition_organizer_editText = findViewById(R.id.competition_organizer_editText);
        competition_reward = findViewById(R.id.competition_reward);
        competition_description = findViewById(R.id.competition_description);
        competition_duration_editText = findViewById(R.id.competition_duration_editText);
        competition_when_results_editText = findViewById(R.id.competition_when_results_editText);
        competition_who_can_take_part_editText = findViewById(R.id.competition_who_can_take_part_editText);
        competition_where_results_editText = findViewById(R.id.competition_where_results_editText);
        competition_additional_info = findViewById(R.id.competition_additional_info);
        competitionCountry = findViewById(R.id.competitionCountry);
        duration_preview = findViewById(R.id.duration_preview);
        competition_must_have_title = findViewById(R.id.competition_must_have_title);
        competition_must_have_organizer = findViewById(R.id.competition_must_have_organizer);
        competition_must_have_reward = findViewById(R.id.competition_must_have_reward);
        competition_must_have_description = findViewById(R.id.competition_must_have_description);
        competition_must_last_at_least_an_hour = findViewById(R.id.competition_must_last_at_least_an_hour);
        competition_must_have_information_when_the_results_will_be_available = findViewById(R.id.competition_must_have_information_when_the_results_will_be_available);
        competition_must_have_information_who_can_take_part_in_competition = findViewById(R.id.competition_must_have_information_who_can_take_part_in_competition);
        competition_must_have_information_where_will_be_results_announced = findViewById(R.id.competition_must_have_information_where_will_be_results_announced);
        check_internet_connection = findViewById(R.id.check_internet_connection);
        success = findViewById(R.id.success);
        error_caused_by_unstable_internet_connection = findViewById(R.id.error_caused_by_unstable_internet_connection);
        done_animation = findViewById(R.id.done_animation);
        your_application_is_being_reviewed = findViewById(R.id.your_application_is_being_reviewed);
        ok = findViewById(R.id.ok);
        scrollview = findViewById(R.id.scrollview);

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

        competitionCountry.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.custom_spinner);

        competition_duration_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(competition_duration_editText.getText().toString().trim().equals("")){
                    duration_preview.setText("");
                }
                else{
                   // Date date = OnlineDate.getDate();
                    long millis = System.currentTimeMillis();
                    String date_and_time = date + " " + millis;

                    Calendar calendar = Calendar.getInstance();
                    try{
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR, Integer.parseInt(competition_duration_editText.getText().toString()));
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
    }

    public void exitAdd(View view) {
        startActivity(new Intent(AddCompetition.this, SelectWhatToAdd.class));
    }

    public void addCompetition(View view){
        if(competition_title_editText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_have_title.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(competition_organizer_editText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_have_organizer.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(competition_reward.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_have_reward.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(competition_description.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_have_description.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(competition_duration_editText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_last_at_least_an_hour.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(competition_when_results_editText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_have_information_when_the_results_will_be_available.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(competition_who_can_take_part_editText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_have_information_who_can_take_part_in_competition.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(competition_where_results_editText.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), competition_must_have_information_where_will_be_results_announced.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }


        String countryName = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(competitionCountry.getSelectedItem().toString(), 1);
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(), check_internet_connection.getText().toString(), Toast.LENGTH_LONG).show();
        }

        if(addresses == null){
            return;
        }

        if (addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }


        Date date = OnlineDate.getDate();
        long millis = System.currentTimeMillis();
        String date_and_time = date + " " + millis;


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, Integer.parseInt(competition_duration_editText.getText().toString()));


        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");

        String modifiedEmail = savedEmail.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");

        AddCompetitionInfo addCompetitionInfo = new AddCompetitionInfo(date_and_time, competition_title_editText.getText().toString(), competition_organizer_editText.getText().toString()
        , savedEmail, competition_reward.getText().toString(), competition_description.getText().toString(), calendar.getTime(), countryName, competition_when_results_editText.getText().toString()
        , competition_who_can_take_part_editText.getText().toString(), competition_where_results_editText.getText().toString(), competition_additional_info.getText().toString());


        scrollview.setVisibility(View.GONE);


        reference = database.getReference("WaitingCompetitions");
        reference.child(date_and_time).setValue(addCompetitionInfo);

        CompanyCompetition companyCompetition = new CompanyCompetition(date_and_time, countryName, calendar.getTime());
        reference = database.getReference("CompanyEmails/" + modifiedEmail);
        reference.child("CompanyCompetition").setValue(companyCompetition);


        done_animation.setVisibility(View.VISIBLE);
        done_animation.setSpeed(1.75F);
        done_animation.playAnimation();

        done_animation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

                ok.setVisibility(View.VISIBLE);
                your_application_is_being_reviewed.setVisibility(View.VISIBLE);

                ok.startAnimation(fadeIn);
                your_application_is_being_reviewed.startAnimation(fadeIn);

            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });


       // Toast.makeText(getApplicationContext(), success.getText().toString(), Toast.LENGTH_LONG).show();




    }

    public void exit (View view){
        Intent intent = new Intent(AddCompetition.this, MapActivityMain.class);
        intent.putExtra("activity", "main");
        startActivity(intent);
    }
    public void fetchDate() {
        OnlineDate.fetchDateAsync(this);
    }

    @Override
    public void onDateFetched(Date date) {

    }
}