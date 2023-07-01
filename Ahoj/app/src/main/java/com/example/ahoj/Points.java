package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.prefs.Preferences;

public class Points extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    TextView user_points, your_points, ad_is_not_ready_yet, come_back;

    int points;

    String your_points_str = "", nick;
    SharedPreferences sharedPreferences;
    private RewardedAd rewardedVideoAd;
    private Button rewardedVideoButton;



    Date currentDate = Calendar.getInstance().getTime();
    String formattedCurrentDate, login_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferences.getString("nick", "");

        your_points = findViewById(R.id.your_points);
        your_points_str = your_points.getText().toString();

        ad_is_not_ready_yet = findViewById(R.id.ad_is_not_ready_yet);

        user_points = findViewById(R.id.textViewUserPoints);
        user_points.setText(nick);

        come_back = findViewById(R.id.come_back);

        reference = database.getReference("Nick/" + nick);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pointsDB = snapshot.child("points").getValue(Integer.class);
                    login_date = snapshot.child("last_login").getValue(String.class);
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

        formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate);

        rewardedVideoButton = findViewById(R.id.rewardedVideoButton);
        if (formattedCurrentDate.equals(login_date)) {
            rewardedVideoButton.setVisibility(View.GONE);
        }


    }

    public void adReward(View view) {
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

                    rewardedVideoButton.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), come_back.getText().toString(), Toast.LENGTH_LONG).show();

                    formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate);
                    reference = database.getReference("Nick/" + nick + "/" + "last_login");
                    reference.setValue(formattedCurrentDate);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), ad_is_not_ready_yet.getText().toString(), Toast.LENGTH_LONG).show();
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