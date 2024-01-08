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

public class MapSettings extends AppCompatActivity {

    TextView map_type_textview, auto_zoom_textview,  on_textview, off_textview, zoom_size_textview;
    Spinner mapTypesSpinner;
    SharedPreferences sharedPreferences;
    Intent intent;
    Boolean is_map_type_textview_visible = false, is_auto_zoom_textview_visible = false, is_zoom_size_textview_visible = false;
    Switch auto_zoom_switch;
    Slider zoom_size_slider;
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
        setContentView(R.layout.activity_map_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        intent = getIntent();

        mapTypesSpinner = findViewById(R.id.mapTypesSpinner);
        map_type_textview = findViewById(R.id.map_type_textview);
        auto_zoom_textview = findViewById(R.id.auto_zoom_textview);
        auto_zoom_switch = findViewById(R.id.auto_zoom_switch);
        on_textview = findViewById(R.id.on_textview);
        off_textview = findViewById(R.id.off_textview);
        zoom_size_textview = findViewById(R.id.zoom_size_textview);
        zoom_size_slider = findViewById(R.id.zoom_size_slider);

        SharedPreferences sharedPreferencesNick = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String nick = sharedPreferencesNick.getString("nick", "");

        SharedPreferences sharedPreferencesUser = getSharedPreferences(nick, Context.MODE_PRIVATE);

        auto_zoom_switch.setChecked(sharedPreferencesUser.getBoolean("auto_zoom", true));
        zoom_size_slider.setValue(sharedPreferencesUser.getInt("zoom_size", 15));

        map_type_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_map_type_textview_visible){
                    mapTypesSpinner.setVisibility(View.VISIBLE);
                    is_map_type_textview_visible = true;
                }
                else{
                    mapTypesSpinner.setVisibility(View.GONE);
                    is_map_type_textview_visible = false;
                }
            }
        });
        auto_zoom_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_auto_zoom_textview_visible){
                    auto_zoom_switch.setVisibility(View.VISIBLE);
                    is_auto_zoom_textview_visible = true;
                }
                else{
                    auto_zoom_switch.setVisibility(View.GONE);
                    is_auto_zoom_textview_visible = false;
                }
            }
        });
        auto_zoom_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        zoom_size_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_zoom_size_textview_visible){
                    zoom_size_slider.setVisibility(View.VISIBLE);
                    is_zoom_size_textview_visible = true;
                }
                else{
                    zoom_size_slider.setVisibility(View.GONE);
                    is_zoom_size_textview_visible = false;
                }
            }
        });
    }

    public void exitSettings (View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }


    public void saveSettings(View view){
       // SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences sharedPreferencesNick = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String nick = sharedPreferencesNick.getString("nick", "");

        SharedPreferences sharedPreferencesUser = getSharedPreferences(nick, Context.MODE_PRIVATE);
        SharedPreferences.Editor editorUser = sharedPreferencesUser.edit();
        if(mapTypesSpinner.getSelectedItemPosition() == 1){
            editorUser.putString("map_type", "hybrid");
            editorUser.apply();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 2){
            editorUser.putString("map_type", "normal");
            editorUser.apply();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 3){
            editorUser.putString("map_type", "satellite");
            editorUser.apply();
        }
        if(mapTypesSpinner.getSelectedItemPosition() == 4){
            editorUser.putString("map_type", "terrain");
            editorUser.apply();
        }

        editorUser.putBoolean("auto_zoom", auto_zoom_switch.isChecked());
        editorUser.apply();

        editorUser.putInt("zoom_size", (int) zoom_size_slider.getValue());
        editorUser.apply();


        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(MapSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

}