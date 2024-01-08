package com.chatoyment.ahoyapp;import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chatoyment.ahoyapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ManageCompetition extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    TextView activity_competition_title, activityCompetitionEndsAt, activityCompetitionOrganizer, activityCompetitionReward, activityCompetitionDescription
            , activityCompetitionEventCountry, activityCompetitionWhenResults, activityCompetitionWhoCanTakePart, activityCompetitionWhereResults, activityCompetitionAdditional
            , deleted, activityStatus, during_the_verification, active, add_results_txt, cancel;

    String date_and_time = "", country = "", email = "";
    Intent activity_intent;
    boolean is_from_competition = true;
    String results_str = "";
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
        setContentView(R.layout.activity_manage_competition);

        activity_intent = getIntent();

        activity_competition_title = findViewById(R.id.activity_competition_title);
        activityCompetitionEndsAt = findViewById(R.id.activityCompetitionEndsAt);
        activityCompetitionOrganizer = findViewById(R.id.activityCompetitionOrganizer);
        activityCompetitionReward = findViewById(R.id.activityCompetitionReward);
        activityCompetitionDescription = findViewById(R.id.activityCompetitionDescription);
        activityCompetitionEventCountry = findViewById(R.id.activityCompetitionEventCountry);
        activityCompetitionWhenResults = findViewById(R.id.activityCompetitionWhenResults);
        activityCompetitionWhoCanTakePart = findViewById(R.id.activityCompetitionWhoCanTakePart);
        activityCompetitionWhereResults = findViewById(R.id.activityCompetitionWhereResults);
        activityCompetitionAdditional = findViewById(R.id.activityCompetitionAdditional);
        deleted = findViewById(R.id.deleted);
        activityStatus = findViewById(R.id.activityStatus);
        during_the_verification = findViewById(R.id.during_the_verification);
        active = findViewById(R.id.active);
        add_results_txt = findViewById(R.id.add_results_txt);
        cancel = findViewById(R.id.cancel);

        date_and_time = activity_intent.getStringExtra("date_and_time");
        country = activity_intent.getStringExtra("country");
        email = activity_intent.getStringExtra("email");
        activityCompetitionEventCountry.setText(activityCompetitionEventCountry.getText().toString() + ": " + country);

        reference = database.getReference("Competitions/" + country + "/" + date_and_time);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    fromWaiting();
                    return;
                }
                else{
                    activity_competition_title.setText(snapshot.child("title").getValue(String.class));
                    activityCompetitionOrganizer.setText(activityCompetitionOrganizer.getText() + " " +  snapshot.child("organizer").getValue(String.class));
                    activityCompetitionReward.setText(activityCompetitionReward.getText() + " " +  snapshot.child("reward").getValue(String.class));
                    activityCompetitionDescription.setText(activityCompetitionDescription.getText() + " " +  snapshot.child("description").getValue(String.class));
                    activityCompetitionWhenResults.setText(activityCompetitionWhenResults.getText() + ": " +  snapshot.child("when_results").getValue(String.class));
                    activityCompetitionWhoCanTakePart.setText(activityCompetitionWhoCanTakePart.getText() + ": " +  snapshot.child("who_can_take_part").getValue(String.class));
                    activityCompetitionWhereResults.setText(activityCompetitionWhereResults.getText() + ": " +  snapshot.child("where_results").getValue(String.class));
                    activityCompetitionAdditional.setText(activityCompetitionAdditional.getText() + " " +  snapshot.child("additional").getValue(String.class));
                    results_str = snapshot.child("results").getValue(String.class);
                   // activityCompetitionEndsAt.setText(activityCompetitionEndsAt.getText() + " " +  snapshot.child("duration").getValue(Date.class));
                    Date competitionDuration = snapshot.child("duration").getValue(Date.class);


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(competitionDuration);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    activityCompetitionEndsAt.setText(activityCompetitionEndsAt.getText().toString() + " " + competitionDuration.getHours() + ":" + competitionDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));


                    activityStatus.setText(activityStatus.getText() + ": " + active.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void fromWaiting(){
        is_from_competition = false;
        reference = database.getReference("WaitingCompetitions/" + date_and_time);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    fromWaiting();
                    return;
                }
                else{
                    activity_competition_title.setText(snapshot.child("title").getValue(String.class));
                    activityCompetitionOrganizer.setText(activityCompetitionOrganizer.getText() + " " +  snapshot.child("organizer").getValue(String.class));
                    activityCompetitionReward.setText(activityCompetitionReward.getText() + " " +  snapshot.child("reward").getValue(String.class));
                    activityCompetitionDescription.setText(activityCompetitionDescription.getText() + " " +  snapshot.child("description").getValue(String.class));
                    activityCompetitionWhenResults.setText(activityCompetitionWhenResults.getText() + " " +  snapshot.child("when_results").getValue(String.class));
                    activityCompetitionWhoCanTakePart.setText(activityCompetitionWhoCanTakePart.getText() + " " +  snapshot.child("who_can_take_part").getValue(String.class));
                    activityCompetitionWhereResults.setText(activityCompetitionWhereResults.getText() + " " +  snapshot.child("where_results").getValue(String.class));
                    activityCompetitionAdditional.setText(activityCompetitionAdditional.getText() + " " +  snapshot.child("additional").getValue(String.class));
                  //  activityCompetitionEndsAt.setText(activityCompetitionEndsAt.getText() + " " +  snapshot.child("duration").getValue(Date.class));

                    // activityCompetitionEndsAt.setText(activityCompetitionEndsAt.getText() + " " +  snapshot.child("duration").getValue(Date.class));
                    Date competitionDuration = snapshot.child("duration").getValue(Date.class);


                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(competitionDuration);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    activityCompetitionEndsAt.setText(activityCompetitionEndsAt.getText().toString() + " " + competitionDuration.getHours() + ":" + competitionDuration.getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));


                    activityStatus.setText(activityStatus.getText() + ": " + during_the_verification.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(View view){
        if(is_from_competition){
            reference = database.getReference("Competitions/" + country + "/" + date_and_time);
            reference.removeValue();

            reference = database.getReference("CompanyEmails/" + email + "/CompanyCompetition");
            reference.removeValue();
        }
        else{
            reference = database.getReference("WaitingCompetitions/" + date_and_time);
            reference.removeValue();

            reference = database.getReference("CompanyEmails/" + email + "/CompanyCompetition");
            reference.removeValue();
        }

        Toast.makeText(getApplicationContext(), deleted.getText().toString(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(ManageCompetition.this, Manage.class));
    }

    public void addResults(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageCompetition.this);
        builder.setTitle(add_results_txt.getText().toString());
        final EditText resultsEditText = new EditText(getApplicationContext());
        resultsEditText.setText(results_str);
        builder.setView(resultsEditText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredResults = resultsEditText.getText().toString();

                reference = database.getReference("Competitions/" + country + "/" + date_and_time);
                reference.child("results").setValue(enteredResults);

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(cancel.getText().toString(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void exit(View view){
        startActivity(new Intent(ManageCompetition.this, Manage.class));
    }
}