package com.chatoyment.ahoyapp;
import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chatoyment.ahoyapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Locale;



public class SettingActivity extends AppCompatActivity {


    TextView textview_done, textview_error, textview_copied;
    LinearLayout support_linear_layout;
    boolean is_support_linear_layout_visible = false;

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
        setContentView(R.layout.activity_setting);

        textview_done = findViewById(R.id.textview_done);
        textview_error = findViewById(R.id.textview_error);
        support_linear_layout = findViewById(R.id.support_linear_layout);
        textview_copied = findViewById(R.id.textview_copied);


        intent = getIntent();

    }

    public void exitSettings (View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(SettingActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(SettingActivity.this, MapActivityMain.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

    public void generalSettings(View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(SettingActivity.this, GeneralSettings.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(SettingActivity.this, GeneralSettings.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

    public void mapSettings(View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(SettingActivity.this, MapSettings.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(SettingActivity.this, MapSettings.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

    public void reconnect(View view){
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 226);

            }
        }
    }

    public void clearCache(View view){
       /* try {
            File cacheDir = getCacheDir();
            if (cacheDir != null) {
                File[] cacheFiles = cacheDir.listFiles();
                if (cacheFiles != null) {
                    for (File cacheFile : cacheFiles) {
                        cacheFile.delete();
                    }
                }
            }
            Toast.makeText(getApplicationContext(), textview_done.getText().toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), textview_error.getText().toString(), Toast.LENGTH_LONG).show();
        }

        */

        deleteCache(getApplicationContext());
    }

    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            boolean success = deleteDir(dir);

            if (success) {
                Toast.makeText(getApplicationContext(), textview_done.getText().toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), textview_error.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            boolean success = true;
            for (int i = 0; i < children.length; i++) {
                success = deleteDir(new File(dir, children[i])) && success;
            }
            return dir.delete() && success;
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    public void logOut(View view){
        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", null);
        editor.apply();

        Intent log_out = new Intent(SettingActivity.this, LoadingScreen.class);
        log_out.putExtra("log_off", "true");
        startActivity(log_out);
    }

    public void FAQ(View view){
        if(intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(SettingActivity.this, FAQ.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(SettingActivity.this, FAQ.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }

    public void showEmailAddress(View view){
        if(!is_support_linear_layout_visible){
            support_linear_layout.setVisibility(View.VISIBLE);
            is_support_linear_layout_visible = true;
        }
        else{
            support_linear_layout.setVisibility(View.GONE);
            is_support_linear_layout_visible = false;
        }
    }

    public void copyAddress(View view){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String email = "AhoyApp4u@gmail.com";
        ClipData clip = ClipData.newPlainText("E-mail", email);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), textview_copied.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    public void accountSettings(View view){
        Intent intent1 = new Intent(SettingActivity.this, AccountSettings.class);
        if(intent.getStringExtra("activity").equals("main")){
            intent1.putExtra("activity", "main");
            startActivity(intent1);
        }
        else{
            intent1.putExtra("activity", "user");
            startActivity(intent1);
        }
    }

    public void statute(View view){
        Intent intent1 = new Intent(SettingActivity.this, Statute.class);
        if(intent.getStringExtra("activity").equals("main")){
            intent1.putExtra("activity", "mainsettings");
            startActivity(intent1);
        }
        else{
            intent1.putExtra("activity", "usersettings");
            startActivity(intent1);
        }
    }
}