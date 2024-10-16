package com.chatoyment.ahoyapp;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chatoyment.ahoyapp.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;

public class Competition extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    String date_and_time, country, additional_copy, nick;

    TextView activity_competition_title, activityCompetitionEndsAt, activityCompetitionOrganizer, activityCompetitionReward, activityCompetitionDescription
            , activityCompetitionWhenResults, activityCompetitionWhoCanTakePart, activityCompetitionWhereResults, activityCompetitionAdditional, copied, textview_results
            , no_results;
    Intent intent;
    AdView adview;
    AppCompatButton copy_button;

    boolean is_results_visible = false, restricted;
    ConstraintLayout constrain_layout_competition, constrain_layout_warning;
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
        setContentView(R.layout.activity_competition);
        intent = getIntent();

        date_and_time = intent.getStringExtra("date_and_time");
        country = intent.getStringExtra("country");
        restricted = intent.getBooleanExtra("restricted", false);

        activity_competition_title = findViewById(R.id.activity_competition_title);
        activityCompetitionEndsAt = findViewById(R.id.activityCompetitionEndsAt);
        activityCompetitionOrganizer = findViewById(R.id.activityCompetitionOrganizer);
        activityCompetitionReward = findViewById(R.id.activityCompetitionReward);
        activityCompetitionDescription = findViewById(R.id.activityCompetitionDescription);
        activityCompetitionWhenResults = findViewById(R.id.activityCompetitionWhenResults);
        activityCompetitionWhoCanTakePart = findViewById(R.id.activityCompetitionWhoCanTakePart);
        activityCompetitionWhereResults = findViewById(R.id.activityCompetitionWhereResults);
        activityCompetitionAdditional = findViewById(R.id.activityCompetitionAdditional);
        adview = findViewById(R.id.adView);
        copy_button = findViewById(R.id.copy_button);
        copied = findViewById(R.id.copied);
        textview_results = findViewById(R.id.textview_results);
        no_results = findViewById(R.id.no_results);
        constrain_layout_competition = findViewById(R.id.constrain_layout_competition);
        constrain_layout_warning = findViewById(R.id.constrain_layout_warning);

        SharedPreferences sharedPreferencesNick = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferencesNick.getString("nick", "");

        SharedPreferences sharedPreferencesUser = getSharedPreferences(nick, Context.MODE_PRIVATE);
        Boolean show_adult_content_warning = sharedPreferencesUser.getBoolean("show_adult_content_warning", true);

        if((restricted && show_adult_content_warning)){
            constrain_layout_competition.setVisibility(View.GONE);
            constrain_layout_warning.setVisibility(View.VISIBLE);
        }

        reference = database.getReference("Competitions/" + country + "/" + date_and_time);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activity_competition_title.setText(snapshot.child("title").getValue(String.class));
                activityCompetitionOrganizer.setText(activityCompetitionOrganizer.getText() + " " +  snapshot.child("organizer").getValue(String.class));
                activityCompetitionReward.setText(activityCompetitionReward.getText() + " " +  snapshot.child("reward").getValue(String.class));
                activityCompetitionDescription.setText(activityCompetitionDescription.getText() + " " +  snapshot.child("description").getValue(String.class));
                activityCompetitionWhenResults.setText(activityCompetitionWhenResults.getText() + ":\n" +  snapshot.child("when_results").getValue(String.class));
                activityCompetitionWhoCanTakePart.setText(activityCompetitionWhoCanTakePart.getText() + ":\n" +  snapshot.child("who_can_take_part").getValue(String.class));
                activityCompetitionWhereResults.setText(activityCompetitionWhereResults.getText() + ":\n" +  snapshot.child("where_results").getValue(String.class));
                activityCompetitionAdditional.setText(activityCompetitionAdditional.getText() + " " +  snapshot.child("additional").getValue(String.class));
                activityCompetitionEndsAt.setText(activityCompetitionEndsAt.getText() + " " +  snapshot.child("duration").getValue(Date.class));

                additional_copy = snapshot.child("additional").getValue(String.class);
                if(additional_copy.trim().equals("")){
                    copy_button.setVisibility(View.GONE);
                    activityCompetitionAdditional.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

    }

    public void copy(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", additional_copy);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplicationContext(), copied.getText().toString(), Toast.LENGTH_LONG).show();
    }

    public void results(View view){
        if(is_results_visible){
            is_results_visible = false;
            textview_results.setVisibility(View.GONE);
            return;
        }
        else{
            is_results_visible = true;
            textview_results.setVisibility(View.VISIBLE);
        }

        reference = database.getReference("Competitions/" + country + "/" + date_and_time);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("results").exists()){
                    textview_results.setText(snapshot.child("results").getValue(String.class));
                }
                else{
                    textview_results.setText(no_results.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void showAdultContent(View view){
        constrain_layout_warning.setVisibility(View.GONE);
        constrain_layout_competition.setVisibility(View.VISIBLE);
    }

    public void exit (View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(Competition.this, CompetitionsActivity.class);
            intent_activity.putExtra("activity", "main");
            intent_activity.putExtra("country", country);
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(Competition.this, CompetitionsActivity.class);
            intent_activity.putExtra("activity", "user");
            intent_activity.putExtra("country", country);
            startActivity(intent_activity);
        }
    }
}