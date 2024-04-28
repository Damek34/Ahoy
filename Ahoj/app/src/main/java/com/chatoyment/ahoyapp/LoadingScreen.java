package com.chatoyment.ahoyapp;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.chatoyment.ahoyapp.R;
import com.example.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.Setup.setup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class LoadingScreen extends AppCompatActivity implements LocationListener {

    private DatabaseReference versionRef;
    private String appVersion = "0.924";

    TextView Textview_app_version_is_not_actual, Textview_please_update_application;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = sharedPreferences2.getString("selectedLanguage", null);

        if (savedLanguage != null) {


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
            } else if (savedLanguage.equals("pl")) {
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
        } else {
            Locale systemLocale = Locale.getDefault();
            String systemLanguage = systemLocale.getLanguage();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (systemLanguage.equals("pl")) {
                editor.putString("selectedLanguage", "pl");
            } else {
                editor.putString("selectedLanguage", "en");
            }

            editor.apply();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        Textview_please_update_application = findViewById(R.id.Textview_please_update_application);
        Textview_app_version_is_not_actual = findViewById(R.id.Textview_app_version_is_not_actual);

        mAuth = FirebaseAuth.getInstance();


        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            Intent no_internet_intent = new Intent(LoadingScreen.this, EnableInternetConnection.class);
            no_internet_intent.putExtra("from_activity", "loadingScreen");
            startActivity(no_internet_intent);
        }

        OnlineDate.fetchDateAsync();


        versionRef = FirebaseDatabase.getInstance().getReference().child("Version");

        versionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firebaseVersion = dataSnapshot.getValue(String.class);

                    if (!firebaseVersion.equals(appVersion)) {
                        showVersionMismatchDialog();

                    } else {
                        continueLoading();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    //}

    private void showVersionMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Textview_app_version_is_not_actual.getText().toString());
        builder.setMessage(Textview_please_update_application.getText().toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(LoadingScreen.this, LoadingScreen.class));
                finish();
            }
        });
        builder.setCancelable(false); // Użytkownik nie może zamknąć dialogu przyciskiem "Back"
        builder.show();
    }

    private void continueLoading()  {
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);

        SharedPreferences sharedPreferencesNick = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String nick = sharedPreferencesNick.getString("nick", "");
        SharedPreferences sharedPreferences2 = getSharedPreferences(nick, Context.MODE_PRIVATE);

        boolean auto_log_out_user = sharedPreferences2.getBoolean("auto_log_out", false);
        if (!auto_log_out_user) {
            String authToken = sharedPreferences.getString("auth_token", null);

            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", "");
            editor.apply();

            if (authToken != null) {
                mAuth.signInWithCustomToken(authToken);

                Intent intent = new Intent(LoadingScreen.this, MapActivityMain.class);
                intent.putExtra("activity", "user");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 1500);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoadingScreen.this, setup.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 1500);
            }
        }
        else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("auth_token", "");
            editor.putString("email", "");
            editor.apply();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingScreen.this, setup.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }, 1500);
        }



    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
     //   super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
       // LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
      //  LocationListener.super.onProviderDisabled(provider);

    }
}