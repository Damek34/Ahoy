package com.chatoyment.ahoyapp;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chatoyment.ahoyapp.R;
import com.example.ahoyapp.OnlyJava.OnlineDate;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Points extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    TextView user_points, your_points, ad_is_not_ready_yet, come_back, you_earned_one_point, preparing_ad_for_progress;

    int points;

    String your_points_str = "", nick;
    SharedPreferences sharedPreferences, sharedPreferencesUser;
    private RewardedAd rewardedVideoAd;
    private Button rewardedVideoButton, collect;

    Date currentDate;
    Calendar checkDate;
    String formattedCurrentDate, login_date;

    ProgressBar progress;
    boolean is_from_progress = false;

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
        setContentView(R.layout.activity_points);

        OnlineDate.fetchDateAsync();
        activity_intent = getIntent();

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferences.getString("nick", "");

        sharedPreferencesUser = getSharedPreferences(nick, Context.MODE_PRIVATE);

        your_points = findViewById(R.id.your_points);
        your_points_str = your_points.getText().toString();

        ad_is_not_ready_yet = findViewById(R.id.ad_is_not_ready_yet);

        user_points = findViewById(R.id.textViewUserPoints);
        user_points.setText(nick);

        come_back = findViewById(R.id.come_back);
        progress = findViewById(R.id.progress);
        collect = findViewById(R.id.collect);
        preparing_ad_for_progress = findViewById(R.id.preparing_ad_for_progress);
        you_earned_one_point = findViewById(R.id.you_earned_one_point);

        rewardedVideoButton = findViewById(R.id.rewardedVideoButton);

        progress.setProgress((int) sharedPreferencesUser.getFloat("distance", 0));

        if(progress.getProgress() < 5000){
            collect.setVisibility(View.GONE);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            Intent intent = new Intent(Points.this, EnableInternetConnection.class);
            intent.putExtra("activity", "user");
            intent.putExtra("from_activity", "points");
            startActivity(intent);
            return;

        }



        reference = database.getReference("Nick/" + nick);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pointsDB = snapshot.child("points").getValue(Integer.class);
                    String login_last_date = snapshot.child("last_ad_bonus").getValue(String.class);

                    if(login_last_date != null){
                        login_date = login_last_date;
                        String[] date_array = login_last_date.split("-");

                        Calendar calendar_last_login = Calendar.getInstance();
                        calendar_last_login.set(Integer.parseInt(date_array[0]), Integer.parseInt(date_array[1]) - 1, Integer.parseInt(date_array[2]), 0, 0, 0);

                        currentDate = OnlineDate.getDate();

                        formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate);


                        Calendar checkDate = Calendar.getInstance();
                        checkDate.setTime(currentDate);

                        checkDate.set(Calendar.HOUR_OF_DAY, 0);
                        checkDate.set(Calendar.MINUTE, 0);
                        checkDate.set(Calendar.SECOND, 0);
                        checkDate.set(Calendar.MILLISECOND, 0);

                        if (checkDate.get(Calendar.YEAR) == calendar_last_login.get(Calendar.YEAR)
                                && checkDate.get(Calendar.MONTH) == calendar_last_login.get(Calendar.MONTH)
                                && checkDate.get(Calendar.DAY_OF_MONTH) == calendar_last_login.get(Calendar.DAY_OF_MONTH)) {
                            rewardedVideoButton.setVisibility(View.GONE);
                        }
                    }




                    loadPoints(pointsDB, login_date);
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


    }

    @SuppressLint("SetTextI18n")
    void loadPoints(int pointsDB, String login_date) {
        points += pointsDB;
        your_points.setText(your_points_str + points);
    }

    public void adReward(View view) {
        is_from_progress = false;
        loadRewardedVideoAd();
        showRewardsAds();
    }

    public void adRewardCollect(View view){
        is_from_progress = true;
        loadRewardedVideoAd();
        showRewardsAds();
    }

    String AdId = "ca-app-pub-3940256099942544/5224354917";

    void loadRewardedVideoAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, AdId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                rewardedVideoAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                rewardedVideoAd = rewardedAd;

                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }
                });
            }
        });
    }

    void showRewardsAds() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.show(Points.this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int amount = rewardItem.getAmount();
                    String type = rewardItem.getType();
                    addPoints();

                    if(is_from_progress){
                        collect.setVisibility(View.GONE);
                        progress.setProgress(0);
                        SharedPreferences.Editor editor = sharedPreferencesUser.edit();
                        editor.putFloat("distance", 0);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), you_earned_one_point.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                    else{
                        rewardedVideoButton.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), come_back.getText().toString(), Toast.LENGTH_LONG).show();

                     //   formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(checkDate);
                        reference = database.getReference("Nick/" + nick + "/" + "last_ad_bonus");
                        reference.setValue(formattedCurrentDate);
                    }

                }
            });
        } else {
            if(is_from_progress){
                Toast.makeText(getApplicationContext(), preparing_ad_for_progress.getText().toString(), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), ad_is_not_ready_yet.getText().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    void addPoints() {
        points++;
        your_points.setText(your_points_str + points);
        reference = database.getReference("Nick/" + nick + "/" + "points");
        reference.setValue(points);
    }

    public void exit(View view) {
        Intent intent = new Intent(Points.this, MapActivityMain.class);
        intent.putExtra("activity", "user");
        startActivity(intent);
    }

    public void leaderboard(View view){
        startActivity(new Intent(Points.this, Leaderboard.class));
    }


}