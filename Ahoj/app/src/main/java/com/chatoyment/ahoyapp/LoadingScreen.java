package com.chatoyment.ahoyapp;
import android.animation.Animator;
import android.annotation.SuppressLint;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.airbnb.lottie.LottieAnimationView;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;
import com.chatoyment.ahoyapp.Setup.setup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Locale;


public class LoadingScreen extends AppCompatActivity implements LocationListener, OnlineDate.OnDateFetchedListener{

    private DatabaseReference versionRef;
    private String appVersion = "0.941";

    TextView Textview_app_version_is_not_actual, Textview_please_update_application, checking_internet_connection, checking_app_version, welcome_to_ahoy;
    ScrollView status_scrollview;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    int green;
    Intent log_off;

    LottieAnimationView loading_animation, done_animation;

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
        status_scrollview = findViewById(R.id.status_scrollview);
       // loading_data = findViewById(R.id.loading_data);
        checking_internet_connection = findViewById(R.id.checking_internet_connection);
        checking_app_version = findViewById(R.id.checking_app_version);
        loading_animation = findViewById(R.id.loading_animation);
        done_animation = findViewById(R.id.done_animation);
        welcome_to_ahoy = findViewById(R.id.welcome_to_ahoy);

        mAuth = FirebaseAuth.getInstance();

        green = ContextCompat.getColor(getApplicationContext(), R.color.green);

        log_off = getIntent();

            if(log_off.getStringExtra("log_off") != null){
                status_scrollview.setVisibility(View.GONE);
            }





        status_scrollview.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            Intent no_internet_intent = new Intent(LoadingScreen.this, EnableInternetConnection.class);
            no_internet_intent.putExtra("from_activity", "loadingScreen");
            startActivity(no_internet_intent);
        }

        final int[] checkingAppVersionY = {checking_app_version.getTop()};

        checking_internet_connection.setTextColor(green);
       // status_scrollview.smoothScrollTo(0, checkingAppVersionY[0]);

        status_scrollview.post(new Runnable() {
            @Override
            public void run() {
                checkingAppVersionY[0] = checking_app_version.getTop();
                status_scrollview.smoothScrollTo(0, checkingAppVersionY[0] - 50);
            }
        });


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
       // int loadingDataY = loading_data.getTop();
       // checking_app_version.setTextColor(green);
       // status_scrollview.smoothScrollTo(0, loadingDataY - 35);

        OnlineDate.fetchDateAsync(this);

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

             //   loading_data.setTextColor(green);

                status_scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        status_scrollview.smoothScrollTo(0, welcome_to_ahoy.getTop() - 35);
                    }
                });

                loading_animation.clearAnimation();
                loading_animation.setVisibility(View.GONE);


                done_animation.setVisibility(View.VISIBLE);
                done_animation.setSpeed(1.75F);
                done_animation.playAnimation();

                done_animation.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {

                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {

                    }
                });


            } else {

//                loading_data.setTextColor(green);
                status_scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        status_scrollview.smoothScrollTo(0, welcome_to_ahoy.getTop() - 35);
                    }
                });

                loading_animation.clearAnimation();
                loading_animation.setVisibility(View.GONE);

                done_animation.setVisibility(View.VISIBLE);
                done_animation.setSpeed(1.75F);
                done_animation.playAnimation();
/*
                done_animation.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        startActivity(new Intent(LoadingScreen.this, setup.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        return;
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {

                    }
                });

 */

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoadingScreen.this, setup.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        return;
                    }
                }, 2000);

            }
        }
        else{

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("auth_token", "");
            editor.putString("email", "");
            editor.apply();


       //     loading_data.setTextColor(green);
            status_scrollview.post(new Runnable() {
                @Override
                public void run() {
                    status_scrollview.smoothScrollTo(0, welcome_to_ahoy.getTop() - 35);
                }
            });


            loading_animation.clearAnimation();
            loading_animation.setVisibility(View.GONE);

            done_animation.setVisibility(View.VISIBLE);
            done_animation.setSpeed(1.75F);
            done_animation.playAnimation();




            done_animation.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    startActivity(new Intent(LoadingScreen.this, setup.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    return;

                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingScreen.this, setup.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    return;
                }
            }, 2000);

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

    @Override
    public void onDateFetched(Date date) {

    }
}