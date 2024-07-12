package com.chatoyment.ahoyapp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.chatoyment.ahoyapp.R;
import com.google.android.material.slider.Slider;

import java.util.Locale;

public class GeneralSettings extends AppCompatActivity {

    TextView duration_of_menu_animation_textview, language_textview, scanning_radius_textview, auto_log_out_textview, on_textview, off_textview,
            notification_after_every_reached_five_kilometers_textview, display_adult_content_textview;
    Spinner languageSpinner;
    SharedPreferences sharedPreferences;
    Slider menu_duration_slider, scanning_radius_slider;
    Switch auto_log_out_switch, notification_after_every_reached_five_kilometers_switch, display_adult_content_switch;
    Boolean is_language_spinner_visible = false , is_menu_slider_visible = false, is_scanning_radius_slider_visible = false, is_auto_log_out_switch_visible = false
            , is_notification_after_every_reached_five_kilometers_switch_visible = false, is_display_adult_content_switch_visible = false;
    View auto_log_out_view;
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
        setContentView(R.layout.activity_general_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        intent = getIntent();
        languageSpinner = findViewById(R.id.changelanguage);

        language_textview = findViewById(R.id.language_textview);
        duration_of_menu_animation_textview = findViewById(R.id.duration_of_menu_animation_textview);
        menu_duration_slider = findViewById(R.id.menu_duration_slider);
        scanning_radius_textview = findViewById(R.id.scanning_radius_textview);
        scanning_radius_slider = findViewById(R.id.scanning_radius_slider);
        auto_log_out_textview = findViewById(R.id.auto_log_out_textview);
        auto_log_out_switch = findViewById(R.id.auto_log_out_switch);
        on_textview = findViewById(R.id.on_textview);
        off_textview = findViewById(R.id.off_textview);
        notification_after_every_reached_five_kilometers_textview = findViewById(R.id.notification_after_every_reached_five_kilometers_textview);
        notification_after_every_reached_five_kilometers_switch = findViewById(R.id.notification_after_every_reached_five_kilometers_switch);
        auto_log_out_view = findViewById(R.id.auto_log_out_view);
        display_adult_content_textview = findViewById(R.id.display_adult_content);
        display_adult_content_switch = findViewById(R.id.display_adult_content_switch);

        SharedPreferences sharedPreferencesNick = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String nick = sharedPreferencesNick.getString("nick", "");

        SharedPreferences sharedPreferencesUser = getSharedPreferences(nick, Context.MODE_PRIVATE);

        menu_duration_slider.setValue(sharedPreferencesUser.getFloat("menu_animation_duration", Float.parseFloat("0.4")));
        scanning_radius_slider.setValue(sharedPreferencesUser.getInt("scanning_radius", Integer.parseInt("20")));
        auto_log_out_switch.setChecked(sharedPreferencesUser.getBoolean("auto_log_out", false));
        notification_after_every_reached_five_kilometers_switch.setChecked(sharedPreferencesUser.getBoolean("notification_every_5_km", true));
        display_adult_content_switch.setChecked(sharedPreferencesUser.getBoolean("display_adult_content", false));


        if(intent.getStringExtra("activity").equals("main")){
            auto_log_out_textview.setVisibility(View.GONE);
            auto_log_out_view.setVisibility(View.GONE);
        }

        scanning_radius_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_scanning_radius_slider_visible){
                    scanning_radius_slider.setVisibility(View.VISIBLE);
                    is_scanning_radius_slider_visible = true;
                }
                else{
                    scanning_radius_slider.setVisibility(View.GONE);
                    is_scanning_radius_slider_visible = false;
                }
            }
        });
        language_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_language_spinner_visible){
                    languageSpinner.setVisibility(View.VISIBLE);
                    is_language_spinner_visible = true;
                }
                else{
                    languageSpinner.setVisibility(View.GONE);
                    is_language_spinner_visible = false;
                }
            }
        });
        auto_log_out_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_auto_log_out_switch_visible){
                    auto_log_out_switch.setVisibility(View.VISIBLE);
                    is_auto_log_out_switch_visible = true;
                }
                else{
                    auto_log_out_switch.setVisibility(View.GONE);
                    is_auto_log_out_switch_visible = false;
                }
            }
        });
        auto_log_out_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Handler switchHandler = new Handler();
            Runnable switchRunnable[] = {null};
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchRunnable[0] != null) {
                    switchHandler.removeCallbacks(switchRunnable[0]);
                }

                switchRunnable[0] = new Runnable() {
                    @Override
                    public void run() {
                        if(isChecked){
                            Toast.makeText(getApplicationContext(), on_textview.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                        else{
                            Toast.makeText(getApplicationContext(), off_textview.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                }

                };
                switchHandler.postDelayed(switchRunnable[0], 750);
            }
        });
        duration_of_menu_animation_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_menu_slider_visible){
                    menu_duration_slider.setVisibility(View.VISIBLE);
                    is_menu_slider_visible = true;
                }
                else{
                    menu_duration_slider.setVisibility(View.GONE);
                    is_menu_slider_visible = false;
                }
            }
        });

        notification_after_every_reached_five_kilometers_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_notification_after_every_reached_five_kilometers_switch_visible){
                    notification_after_every_reached_five_kilometers_switch.setVisibility(View.VISIBLE);
                    is_notification_after_every_reached_five_kilometers_switch_visible = true;
                }
                else{
                    notification_after_every_reached_five_kilometers_switch.setVisibility(View.GONE);
                    is_notification_after_every_reached_five_kilometers_switch_visible = false;
                }
            }
        });

        display_adult_content_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!is_display_adult_content_switch_visible){
                    display_adult_content_switch.setVisibility(View.VISIBLE);
                    is_display_adult_content_switch_visible = true;
                }
                else{
                    display_adult_content_switch.setVisibility(View.GONE);
                    is_display_adult_content_switch_visible = false;
                }
            }
        });


    }




    public void exitSettings (View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(GeneralSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(GeneralSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

    public void saveSettings(View view){
        Locale locale = null;
        if (languageSpinner.getSelectedItemPosition() == 1) {
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

        } else if (languageSpinner.getSelectedItemPosition() == 2) {
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (languageSpinner.getSelectedItemPosition() == 1) {
            editor.putString("selectedLanguage", "en");
            editor.apply();
        } else if (languageSpinner.getSelectedItemPosition() == 2) {
            editor.putString("selectedLanguage", "pl");
            editor.apply();
        }

        SharedPreferences sharedPreferencesNick = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String nick = sharedPreferencesNick.getString("nick", "");

        SharedPreferences sharedPreferencesUser = getSharedPreferences(nick, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorUser = sharedPreferencesUser.edit();

        editorUser.putFloat("menu_animation_duration", Float.parseFloat(String.valueOf(menu_duration_slider.getValue())));
        editorUser.apply();

        editorUser.putInt("scanning_radius", (int) scanning_radius_slider.getValue());
        editorUser.apply();

        editorUser.putBoolean("auto_log_out", auto_log_out_switch.isChecked());
        editorUser.apply();

        editorUser.putBoolean("notification_every_5_km", notification_after_every_reached_five_kilometers_switch.isChecked());
        editorUser.apply();

        editorUser.putBoolean("display_adult_content", display_adult_content_switch.isChecked());
        editorUser.apply();


        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(GeneralSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(GeneralSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }

    }
}