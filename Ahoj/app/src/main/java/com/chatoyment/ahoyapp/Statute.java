package com.chatoyment.ahoyapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.chatoyment.ahoyapp.R;
import com.chatoyment.ahoyapp.Setup.Register;
import com.chatoyment.ahoyapp.Setup.RegisterOrLogin;
import com.chatoyment.ahoyapp.Setup.RegisterUser;
import com.chatoyment.ahoyapp.Setup.setup;

import java.util.Locale;

public class Statute extends AppCompatActivity {



    Intent intent;
    Button faq_btn;
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
        setContentView(R.layout.activity_statute);
        intent = getIntent();
        faq_btn = findViewById(R.id.faq_btn);
        if(!intent.getStringExtra("activity").equals("setup") && !intent.getStringExtra("activity").equals("register") && !intent.getStringExtra("activity").equals("registerUser")){
            faq_btn.setVisibility(View.GONE);
        }
/*
        if(intent.getStringExtra("activity").equals("AddLocalEvent")){
        nameV = intent.getStringExtra("event_name");
        descV = intent.getStringExtra("event_desc");
       /locationV = intent.getStringExtra("localization");
        company_nameV = intent.getStringExtra("company_name");
        durationStr = intent.getStringExtra("duration");
        additionalV = intent.getStringExtra("additional");
        }

        if(intent.getStringExtra("activity").equals("AddAnnouncement")){

            announcement_desc = intent.getStringExtra("event_desc");
            announcement_company_name = intent.getStringExtra("company_name");
            announcement_duration = intent.getStringExtra("duration");
            announcement_additional = intent.getStringExtra("additional");
            restricted = intent.getStringExtra("restricted");

        }

 */

    }

    public void exit(View view) {
        if(intent.getStringExtra("activity").equals("AddLocalEvent")){

        Intent intent2 = new Intent(Statute.this, AddLocalEvent.class);

        intent2.putExtra("event_name", intent.getStringExtra("event_name"));
        intent2.putExtra("event_desc", intent.getStringExtra("event_desc"));
        intent2.putExtra("localization", intent.getStringExtra("localization"));
        intent2.putExtra("company_name", intent.getStringExtra("company_name"));
        intent2.putExtra("duration", intent.getStringExtra("duration"));
        intent2.putExtra("additional", intent.getStringExtra("additional"));
        intent2.putExtra("restricted", intent.getStringExtra("restricted"));


            if(intent.getStringExtra("isSocial").equals(true)){
                intent2.putExtra("isSocial", "true");
            }
            else{
                intent2.putExtra("isSocial", "false");
            }

        startActivity(intent2);
            return;
        }
        if(intent.getStringExtra("activity").equals("setup")){
            startActivity(new Intent(Statute.this, setup.class));
            return;
        }

        if(intent.getStringExtra("activity").equals("AddAnnouncement")){
            Intent statue = new Intent(Statute.this, AddAnnouncement.class);

            statue.putExtra("event_desc", intent.getStringExtra("event_desc"));
            statue.putExtra("company_name", intent.getStringExtra("company_name"));
            statue.putExtra("duration",intent.getStringExtra("duration"));
            statue.putExtra("additional", intent.getStringExtra("additional"));
            statue.putExtra("restricted", intent.getStringExtra("restricted"));

            if(intent.getStringExtra("isSocial").equals(true)){
                statue.putExtra("isSocial", "true");
            }
            else{
                statue.putExtra("isSocial", "false");
            }

            startActivity(statue);
            return;
        }
        if(intent.getStringExtra("activity").equals("AddCompetition")){
            Intent statue = new Intent(Statute.this, AddCompetition.class);

            statue.putExtra("activity", "tos");
            statue.putExtra("title", intent.getStringExtra("title"));
            statue.putExtra("organizer", intent.getStringExtra("organizer"));
            statue.putExtra("reward", intent.getStringExtra("reward"));
            statue.putExtra("desc", intent.getStringExtra("desc"));
            statue.putExtra("when_results", intent.getStringExtra("when_results"));
            statue.putExtra("who_can_take_part", intent.getStringExtra("who_can_take_part"));
            statue.putExtra("where_results", intent.getStringExtra("where_results"));
            statue.putExtra("duration", intent.getStringExtra("duration"));
            statue.putExtra("additional", intent.getStringExtra("additional"));
            statue.putExtra("restricted", intent.getStringExtra("restricted"));



            startActivity(statue);
            return;
        }

        if(intent.getStringExtra("activity").equals("mainsettings")){
            Intent intent2 = new Intent(Statute.this, SettingActivity.class);
            intent2.putExtra("activity", "main");
            startActivity(intent2);
            return;
        }
        if(intent.getStringExtra("activity").equals("usersettings")){
            Intent intent2 = new Intent(Statute.this, SettingActivity.class);
            intent2.putExtra("activity", "user");
            startActivity(intent2);
            return;
        }

        if(intent.getStringExtra("activity").equals("register")){
            startActivity(new Intent(Statute.this, Register.class));
            return;
        }

        if(intent.getStringExtra("activity").equals("registerUser")){
            startActivity(new Intent(Statute.this, RegisterUser.class));
            return;
        }


        if(intent.getStringExtra("activity").equals("register_or_login")){
            Intent intent1 = new Intent(Statute.this, RegisterOrLogin.class);
            if(intent.getStringExtra("activity_type").equals("main")){
                intent1.putExtra("activity", "main");
            }
            else{
                intent1.putExtra("activity", "user");
            }

            startActivity(intent1);
            return;
        }

        //-----------------------------------przebudowac
        if(intent.getStringExtra("activity").equals("faqMain")){
            Intent intent2 = new Intent(Statute.this, FAQ.class);
            intent2.putExtra("activity", "main");
            startActivity(intent2);
            return;
        }
        else if(intent.getStringExtra("activity").equals("faqUser")){
            Intent intent2 = new Intent(Statute.this, FAQ.class);
            intent2.putExtra("activity", "user");
            startActivity(intent2);
            return;
        }
        else{
            startActivity(new Intent(Statute.this, FAQ.class));
            return;
        }


    }
    public void faq(View view){
        if(intent.getStringExtra("activity").equals("setup")){
            Intent intent1 = new Intent(Statute.this, FAQ.class);
            intent1.putExtra("activity", "setup");
            startActivity(intent1);
        }

        if(intent.getStringExtra("activity").equals("register")){
            Intent intent1 = new Intent(Statute.this, FAQ.class);
            intent1.putExtra("activity", "register");
            startActivity(intent1);
        }

        if(intent.getStringExtra("activity").equals("registerUser")){
            Intent intent1 = new Intent(Statute.this, FAQ.class);
            intent1.putExtra("activity", "registerUser");
            startActivity(intent1);
        }

    }
}