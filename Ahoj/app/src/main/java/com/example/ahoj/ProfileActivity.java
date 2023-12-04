package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUser, your_points, visited_events, visited_social_events, visited_promo_events, description, no_description, max_100, total_distance;
    SharedPreferences sharedPreferences;
    String nick, description_str, mynick;
    AdView adview;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    Integer visited_eventsV, visited_social_eventsV;
    Boolean is_visit_details_visible = false;
    Button edit_description_btn, friends_button;
    ConstraintLayout constrain_layout_description;
    EditText editText_description;
    Intent activity_intent;
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
        setContentView(R.layout.activity_profile);

        textViewUser = findViewById(R.id.textViewUser);
        your_points = findViewById(R.id.your_points);
        visited_events = findViewById(R.id.visited_events);
        visited_promo_events = findViewById(R.id.visited_promo_events);
        visited_social_events = findViewById(R.id.visited_social_events);
        description = findViewById(R.id.description);
        no_description = findViewById(R.id.no_description);
        edit_description_btn = findViewById(R.id.edit_description_btn);
        constrain_layout_description = findViewById(R.id.constrain_layout_description);
        editText_description = findViewById(R.id.editText_description);
        max_100 = findViewById(R.id.max_100);
        friends_button = findViewById(R.id.friends_button);
        total_distance = findViewById(R.id.total_distance);

        activity_intent = getIntent();

        description_str = description.getText().toString();

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        mynick = sharedPreferences.getString("nick", "");
        nick = activity_intent.getStringExtra("nick");

        textViewUser.setText(nick);


        if(!nick.equals(mynick)){
            edit_description_btn.setVisibility(View.GONE);
            friends_button.setVisibility(View.GONE);
        }

        reference = database.getReference("Nick/" + nick);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    your_points.setText(your_points.getText().toString() + ": " + snapshot.child("points").getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference = database.getReference("Nick/" + nick);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //---------visited-------------
                    visited_eventsV = snapshot.child("VisitedEventsNumber").getValue(Integer.class);
                    visited_social_eventsV = snapshot.child("VisitedSocialEventsNumber").getValue(Integer.class);

                    if(visited_eventsV == null){
                        visited_eventsV = 0;
                    }
                    if(visited_social_eventsV == null){
                        visited_social_eventsV = 0;
                    }

                    int total_visited = visited_eventsV + visited_social_eventsV;

                    visited_events.setText(visited_events.getText() + ": " + total_visited);
                    visited_social_events.setText(visited_social_events.getText() + ": " + visited_social_eventsV);
                    visited_promo_events.setText(visited_promo_events.getText() + ": " + visited_eventsV);


                    //---------------description-------------------
                    String description_str_temp = snapshot.child("description").getValue(String.class);

                    if(description_str_temp == null){
                        description_str_temp = " ";
                    }

                    if(description_str_temp.trim().isEmpty()){
                        description.setText(description_str + " " + no_description.getText().toString());
                    }
                    else{
                        description.setText(description_str + " " + description_str_temp);
                    }

                    //--------------distance-----------------
                    Integer total_distanceV = snapshot.child("distance").getValue(Integer.class);
                    if(total_distanceV == null){
                        total_distanceV = 0;
                    }
                    total_distance.setText(total_distance.getText().toString() + ": " + total_distanceV + "m");


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
        adview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);
    }

    public void visitedEventsDetail(View view){
        if(is_visit_details_visible){
            is_visit_details_visible = false;
            visited_social_events.setVisibility(View.GONE);
            visited_promo_events.setVisibility(View.GONE);
        }
        else{
            is_visit_details_visible = true;
            visited_social_events.setVisibility(View.VISIBLE);
            visited_promo_events.setVisibility(View.VISIBLE);
        }
    }

    public void editDescription(View view){
        edit_description_btn.setVisibility(View.GONE);
        constrain_layout_description.setVisibility(View.VISIBLE);
    }

    public void cancelDescription(View view){
        edit_description_btn.setVisibility(View.VISIBLE);
        constrain_layout_description.setVisibility(View.GONE);
        editText_description.setText("");
    }

    public void saveDescription(View view){
        if(editText_description.getText().toString().length() <= 100){
            reference = database.getReference("Nick/" + nick + "/description");
            reference.setValue(editText_description.getText().toString());

            description.setText(description_str + " " + editText_description.getText().toString());

            edit_description_btn.setVisibility(View.VISIBLE);
            constrain_layout_description.setVisibility(View.GONE);
        }
        else{
            Toast.makeText(getApplicationContext(), max_100.getText().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void friends(View view){
        startActivity(new Intent(ProfileActivity.this, FriendsActivity.class));
    }

    public void exit(View view) {

        if(!nick.equals(mynick)){
            startActivity(new Intent(ProfileActivity.this, FriendsActivity.class));
        }
        else{
            Intent intent = new Intent(ProfileActivity.this, MapActivityMain.class);
            intent.putExtra("activity", "user");
            startActivity(intent);
        }

    }
}