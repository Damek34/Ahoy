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
import android.widget.Button;

import com.example.ahoj.Setup.setup;

import java.util.Locale;

public class FAQ extends AppCompatActivity {
    Button terms;
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
        setContentView(R.layout.activity_faq);

        intent = getIntent();
        terms = findViewById(R.id.terms);
        if(intent.getStringExtra("activity").equals("setup")){
            terms.setVisibility(View.GONE);
        }
    }

    public void statute(View view){
        Intent statute = new Intent(FAQ.this, Statute.class);
        if(intent.getStringExtra("activity").equals("main")){
            statute.putExtra("activity", "faqMain");
        } else if (intent.getStringExtra("activity").equals("user")) {
            statute.putExtra("activity", "faqUser");
        }
        else{
            statute.putExtra("activity", "faq");
        }
        startActivity(statute);
    }


    public void exit (View view){
        if(intent.getStringExtra("activity").equals("setup")){
            Intent intent_activity = new Intent(FAQ.this, Statute.class);
            intent_activity.putExtra("activity", "setup");
            startActivity(intent_activity);
        }
        else if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(FAQ.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else if(intent.getStringExtra("activity").equals("user")){
            Intent intent_activity = new Intent(FAQ.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
        else{
            startActivity(new Intent(FAQ.this, setup.class));
        }
    }
}