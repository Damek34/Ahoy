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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    AdView adview;

    String date_and_time = "", country = "", eventDescription, eventCompanyName, additional, nick, isavailable, organizer, social_mode;
    TextView event_company, event_desc, event_additional, thanks_for_joining, copied, come_to_event_to_see_more_information;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    SharedPreferences sharedPreferences;

    Intent intent;
    Toolbar toolbaradditional;
    Button report_event_btn;

    @SuppressLint("SetTextI18n")
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
        setContentView(R.layout.activity_event);

        intent = getIntent();

        thanks_for_joining = findViewById(R.id.points_for_event);

        TextView event_name = (TextView) findViewById(R.id.activity_event_name);
        TextView event_ends_in = (TextView) findViewById(R.id.activityEventEventEndsAt);
        TextView event_location = (TextView) findViewById(R.id.activityEventEventLocation);
        event_company = (TextView) findViewById(R.id.activityEventEventCompanyName);
        event_desc = (TextView) findViewById(R.id.activityEventEventDescription);
        event_additional = (TextView) findViewById(R.id.activityEventEventAdditional);
        come_to_event_to_see_more_information = findViewById(R.id.come_to_event_to_see_more_information);
        report_event_btn = findViewById(R.id.report_event_btn);

        copied = findViewById(R.id.copied);


        date_and_time = getIntent().getStringExtra("DateAndTime");
        country = getIntent().getStringExtra("Country");
        organizer = getIntent().getStringExtra("organizer");
        social_mode = getIntent().getStringExtra("social_mode");

        event_name.setText(getIntent().getStringExtra("Name"));
        event_ends_in.setText(event_ends_in.getText() + " " + getIntent().getStringExtra("Duration"));
        event_location.setText(event_location.getText() + " " + getIntent().getStringExtra("Localization"));
        isavailable = getIntent().getStringExtra("isavailable");

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferences.getString("nick", "");

        if(intent.getStringExtra("activity").equals("main")){
            report_event_btn.setVisibility(View.GONE);
        }

        if(social_mode.equals("false")){
            reference = database.getReference("Event/" + country + "/" + date_and_time);
        }
        else{
            reference = database.getReference("SocialEvent/" + country + "/" + date_and_time);
        }


        if(isavailable.equals("true")){
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventDescription = snapshot.child("event_description").getValue(String.class);
                    eventCompanyName = snapshot.child("event_company_name").getValue(String.class);
                    additional = snapshot.child("event_additional").getValue(String.class);

                    load();
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
        }
        else{
            toolbaradditional.setVisibility(View.GONE);
            event_desc.setVisibility(View.GONE);
            come_to_event_to_see_more_information.setVisibility(View.VISIBLE);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventCompanyName = snapshot.child("event_company_name").getValue(String.class);
                    loadNotAvailable();
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
        }

        adview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);


    }

    void load(){
        event_company.setText(event_company.getText() + " " + eventCompanyName);
        event_desc.setText(event_desc.getText() +  " " + eventDescription);

        if(!additional.trim().isEmpty() ){
            event_additional.setText(event_additional.getText() + " " + additional);

        }
        else{
            toolbaradditional.setVisibility(View.GONE);
        }

        if(intent.getStringExtra("activity").equals("user")){
            shouldGetPoints();
        }
    }

    void loadNotAvailable(){
        event_company.setText(event_company.getText() + " " + eventCompanyName);
        toolbaradditional.setVisibility(View.GONE);
    }




    void shouldGetPoints(){
        if(social_mode.equals("false")){
            reference = database.getReference("Event/" + country + "/" + date_and_time +"/" + "Nick");
        }
        else{
            reference = database.getReference("SocialEvent/" + country + "/" + date_and_time +"/" + "Nick");
        }

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(nick).exists()){
                    reference.child(nick).setValue(nick);
                    addPoints();
                    increaseVisitedEventsNumber();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void addPoints(){
        reference = database.getReference("Nick/" + nick);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pointsDB = snapshot.child("points").getValue(Integer.class);
                    pointsDB += 2;

                    reference = database.getReference("Nick/" + nick + "/" + "points");
                    reference.setValue(pointsDB);

                    Toast.makeText(getApplicationContext(), thanks_for_joining.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void increaseVisitedEventsNumber(){
        final int[] number = {0};
        if(social_mode.equals("false")){
            reference = database.getReference("Nick/" + nick + "/VisitedEventsNumber");
        }
        else{
            reference = database.getReference("Nick/" + nick + "/VisitedSocialEventsNumber");
        }

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    number[0] = snapshot.getValue(Integer.class);
                    reference.setValue(number[0] + 1);
                }
                else{
                    reference.setValue(1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void report(View view){
        if(intent.getStringExtra("activity").equals("user")){
            Intent intent1 = new Intent(EventActivity.this, ReportActivity.class);
            intent1.putExtra("activity", "user");
            intent1.putExtra("DateAndTime", date_and_time);
            intent1.putExtra("country", country);
            intent1.putExtra("organizer", organizer);
            intent1.putExtra("social_mode", social_mode);
            intent1.putExtra("isavailable", isavailable);
            intent1.putExtra("Localization", intent.getStringExtra("Localization"));
            intent1.putExtra("Duration", intent.getStringExtra("Duration"));
            intent1.putExtra("Name", intent.getStringExtra("Name"));

            intent1.putExtra("announcement_or_event", "event");

            startActivity(intent1);
        }
        else{
            Intent intent1 = new Intent(EventActivity.this, ReportActivity.class);
            intent1.putExtra("activity", "main");
            intent1.putExtra("DateAndTime", date_and_time);
            intent1.putExtra("country", country);
            intent1.putExtra("organizer", organizer);
            intent1.putExtra("social_mode", social_mode);
            intent1.putExtra("isavailable", isavailable);
            intent1.putExtra("Localization", intent.getStringExtra("Localization"));
            intent1.putExtra("Duration", intent.getStringExtra("Duration"));
            intent1.putExtra("Name", intent.getStringExtra("Name"));


            intent1.putExtra("announcement_or_event", "event");

            startActivity(intent1);
        }
    }
    public void copy(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", additional);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getApplicationContext(), copied.getText().toString(), Toast.LENGTH_LONG).show();
    }


    public void exit (View view){
        if(intent.getStringExtra("activity").equals("user")){
            Intent intent1 = new Intent(EventActivity.this, MapActivityMain.class);
            intent1.putExtra("activity", "user");
            startActivity(intent1);
        }
        else{
            Intent intent1 = new Intent(EventActivity.this, MapActivityMain.class);
            intent1.putExtra("activity", "main");
            startActivity(intent1);
        }
    }
}