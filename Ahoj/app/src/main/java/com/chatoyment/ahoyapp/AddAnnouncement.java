package com.chatoyment.ahoyapp;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.chatoyment.ahoyapp.OnlyJava.AddAnnouncementInfo;
import com.chatoyment.ahoyapp.OnlyJava.CompanyAnnouncement;
import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.OnlyJava.ForbiddenWords;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAnnouncement extends AppCompatActivity implements OnlineDate.OnDateFetchedListener {

    EditText announcement_desc, announcement_company_name, announcement_duration, announcement_additional;
    TextView must_have_company, must_have_desc, must_have_hour, add, check_internet_connection, announcement_will_ends, duration_preview,
            error_caused_by_unstable_internet_connection, your_application_is_being_reviewed, checkbox_adult_info, age_restricted, application_contains_forbidden_expression;
    CheckBox checkbox_adult;
    Spinner country;
    String countryName, encryptedEmail, email_date_and_time, available_to_everyone, intent_restricted;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Intent activity_intent;

    Intent social_intent;

    Date date;
    ScrollView scrollview;
    Button tos, ok;
    LottieAnimationView done_animation;
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
        setContentView(R.layout.activity_add_announcement);

        OnlineDate.fetchDateAsync(this);

        social_intent = getIntent();

        announcement_desc = findViewById(R.id.announcement_description);
        announcement_company_name = findViewById(R.id.announcement_company_name);
        announcement_duration = findViewById(R.id.announcement_duration);
        announcement_additional = findViewById(R.id.announcement_additional_info);
        announcement_will_ends = findViewById(R.id.announcement_will_ends);

        must_have_company = findViewById(R.id.textViewMustHaveCompanyName);
        must_have_desc = findViewById(R.id.textViewMustHaveDesc);
        must_have_hour = findViewById(R.id.textViewMustLastAHour);
        add = findViewById(R.id.add_announcementPreview);
        duration_preview = findViewById(R.id.duration_preview);
        error_caused_by_unstable_internet_connection = findViewById(R.id.error_caused_by_unstable_internet_connection);
        country = findViewById(R.id.announcementCountry);
        check_internet_connection = findViewById(R.id.check_internet_connection);
        scrollview = findViewById(R.id.scrollview);
        tos = findViewById(R.id.tos);
        done_animation = findViewById(R.id.done_animation);
        ok = findViewById(R.id.ok);
        your_application_is_being_reviewed = findViewById(R.id.your_application_is_being_reviewed);
        checkbox_adult = findViewById(R.id.checkbox_adult);
        checkbox_adult_info = findViewById(R.id.checkbox_adult_info);
        age_restricted = findViewById(R.id.age_restricted);
        application_contains_forbidden_expression = findViewById(R.id.application_contains_forbidden_expression);

        available_to_everyone = checkbox_adult_info.getText().toString();

        date = OnlineDate.getDate();

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

        country.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.custom_spinner);


        activity_intent = getIntent();
        String test_name = activity_intent.getStringExtra("company_name");

        if(test_name != null){
            announcement_company_name.setText(test_name);
            announcement_desc.setText(activity_intent.getStringExtra("event_desc"));
            announcement_duration.setText(activity_intent.getStringExtra("duration"));
            announcement_additional.setText(activity_intent.getStringExtra("additional"));
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

        announcement_duration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!announcement_duration.getText().toString().isEmpty()){
                    char[] duration_char = announcement_duration.getText().toString().toCharArray();
                    if(String.valueOf(duration_char[0]).equals("0")){
                        duration_preview.setText("");
                        announcement_duration.setText("");
                        return;
                    }
                }


                if(announcement_duration.getText().toString().trim().equals("")){
                    duration_preview.setText("");
                }
                else{
                 //   Date date = OnlineDate.getDate();
                    long millis = System.currentTimeMillis();
                    String date_and_time = date + " " + millis;


                    Calendar calendar = Calendar.getInstance();
                    try{
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR, Integer.parseInt(announcement_duration.getText().toString()));
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

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        encryptedEmail = EncryptionHelper.encrypt(savedEmail);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CompanyEmails");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                    String email = companySnapshot.child("email").getValue(String.class);
                    if (email.equals(encryptedEmail)) {
                        email_date_and_time = companySnapshot.getKey();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void exitAdd(View view) {
        startActivity(new Intent(AddAnnouncement.this, SelectWhatToAdd.class));
    }

    public void addAnnouncement(View view){
        if(announcement_company_name.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), must_have_company.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(announcement_desc.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), must_have_desc.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(announcement_duration.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), must_have_hour.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        boolean have_forbidden_words = ForbiddenWords.containsForbiddenWord(announcement_company_name);
        if(!have_forbidden_words){
            have_forbidden_words = ForbiddenWords.containsForbiddenWord(announcement_desc);
            if(!have_forbidden_words){
                have_forbidden_words = ForbiddenWords.containsForbiddenWord(announcement_additional);
                if(!have_forbidden_words){
                    addToDataBase();
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

    private void addToDataBase(){
        countryName = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(country.getSelectedItem().toString(), 1);
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(), check_internet_connection.getText().toString(), Toast.LENGTH_LONG).show();
        }

        if(addresses == null){
            return;
        }

        if (addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }



        //Date date = new Date();

        Date date = OnlineDate.getDate();
        long millis = System.currentTimeMillis();
        String date_and_time = date + " " + millis;


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, Integer.parseInt(announcement_duration.getText().toString()));
        // calendar.add(Calendar.SECOND, 34);


        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String encryptedEmail = EncryptionHelper.encrypt(savedEmail);

        if(social_intent.getStringExtra("isSocial").equals("true")){
            reference = database.getReference("WaitingSocialAnnouncements");
        }
        else{
            reference = database.getReference("WaitingAnnouncements");
        }

        scrollview.setVisibility(View.GONE);
        tos.setVisibility(View.GONE);


        AddAnnouncementInfo newAnnouncement = new AddAnnouncementInfo(date_and_time, announcement_company_name.getText().toString(), announcement_desc.getText().toString(), calendar.getTime(), announcement_additional.getText().toString(), countryName, encryptedEmail, checkbox_adult.isChecked());
        reference.child(date_and_time).setValue(newAnnouncement);

/*
        if(calendar.get(Calendar.MINUTE) < 10){
            Toast.makeText(this, add.getText().toString() + ". " + announcement_will_ends.getText().toString() + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
                    + ", " + calendar.get(Calendar.HOUR) + ":" + "0" + calendar.get(Calendar.MINUTE) , Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, add.getText().toString() + ". " + announcement_will_ends.getText().toString() + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
                    + ", " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) , Toast.LENGTH_LONG).show();
        }


 */

      /*  String modifiedEmail = savedEmail.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");



       */

        reference = database.getReference("CompanyEmails/" + email_date_and_time);

        CompanyAnnouncement companyAnnouncement = new CompanyAnnouncement(date_and_time, calendar.getTime(), countryName);
        if(social_intent.getStringExtra("isSocial").equals("true")){
            reference.child("CompanySocialAnnouncement").setValue(companyAnnouncement);

        }
        else{
            reference.child("CompanyAnnouncement").setValue(companyAnnouncement);
        }

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
    }

    public void exit(View view){
        Intent intent = new Intent(AddAnnouncement.this, MapActivityMain.class);
        intent.putExtra("activity", "main");
        startActivity(intent);
    }

    public void statute(View view){
        Intent statue = new Intent(AddAnnouncement.this, Statute.class);

        statue.putExtra("activity", "AddAnnouncement");
        statue.putExtra("event_desc", announcement_desc.getText().toString());
        statue.putExtra("company_name", announcement_company_name.getText().toString());
        statue.putExtra("duration", announcement_duration.getText().toString());
        statue.putExtra("additional", announcement_additional.getText().toString());
        statue.putExtra("restricted", String.valueOf(checkbox_adult.isChecked()));
        if(social_intent.getStringExtra("isSocial").equals(true)){
            statue.putExtra("isSocial", "true");
        }
        else{
            statue.putExtra("isSocial", "false");
        }

        startActivity(statue);
    }





    public void fetchDate() {
        OnlineDate.fetchDateAsync(this);
    }
    @Override
    public void onDateFetched(Date date) {

    }
}