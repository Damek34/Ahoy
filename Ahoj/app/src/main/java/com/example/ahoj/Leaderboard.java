package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import DatabaseFiles.CountryAge.User;

public class Leaderboard extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView leaderboardTextView, point, points, points5, leaderboard, downloadingLeaderboard;

    AdView adview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        point = findViewById(R.id.point);
        points = findViewById(R.id.points);
        points5 = findViewById(R.id.points5);
        leaderboard = findViewById(R.id.Leaderboard);
        downloadingLeaderboard = findViewById(R.id.downloadingLeaderboard);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });


        adview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);


        leaderboardTextView = findViewById(R.id.leaderboardTextView);

        databaseReference = FirebaseDatabase.getInstance().getReference("Nick");

        Query query = databaseReference.orderByChild("points").startAt(1).limitToLast(10);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserLeaderboard> userList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nick = snapshot.child("nick").getValue(String.class);
                    int points = snapshot.child("points").getValue(Integer.class);

                    UserLeaderboard user = new UserLeaderboard(nick, points);
                    userList.add(user);
                }
                downloadingLeaderboard.setText(leaderboard.getText().toString());

                // Sortowanie użytkowników według liczby punktów
                Collections.sort(userList, new Comparator<UserLeaderboard>() {
                    @Override
                    public int compare(UserLeaderboard user1, UserLeaderboard user2) {
                        return Integer.compare(user2.getPoints(), user1.getPoints());
                    }
                });

                // Tworzenie tekstu do wyświetlenia w TextView
                StringBuilder leaderboardText = new StringBuilder();
                for (int i = 0; i < userList.size(); i++) {
                    UserLeaderboard user = userList.get(i);
                    String leaderboardEntry = "";
                    if(user.getPoints() == 1){
                        leaderboardEntry = (i + 1) + ": " + user.getNick() + " - " + user.getPoints() + " "  + point.getText().toString() + "\n\n";
                    }
                    else if(user.getPoints() > 1 && user.getPoints() < 5){
                        leaderboardEntry = (i + 1) + ": " + user.getNick() + " - " + user.getPoints() + " " + points.getText().toString() + "\n\n" ;
                    }
                    else if(user.getPoints() > 4){
                        leaderboardEntry += (i + 1) + ": " + user.getNick() + " - " + user.getPoints() + " " + points5.getText().toString() + "\n\n";
                    }
                    leaderboardText.append(leaderboardEntry);
                }

                leaderboardTextView.setTextSize(24);
                leaderboardTextView.setText(leaderboardText.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                leaderboardTextView.setText("Error " + databaseError.getMessage());
            }
        });





    }

    private static class UserLeaderboard {
        private String nick;
        private int points;

        public UserLeaderboard(String nick, int points) {
            this.nick = nick;
            this.points = points;
        }

        public String getNick() {
            return nick;
        }

        public int getPoints() {
            return points;
        }
    }

    public void exit(View view){
        startActivity(new Intent(Leaderboard.this, Points.class));
    }



}