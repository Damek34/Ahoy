package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;



public class Leaderboard extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView leaderboardTextView, point, points, points5, leaderboard, downloadingLeaderboard;
    private Button button_friends, button_world;
    String nick, downloading;
    SharedPreferences sharedPreferences;
    AdView adview;
    ArrayList<String> friendsList;
    ArrayList<UserLeaderboard> friendsLeaderboard;
    int my_points;
    LinearLayout linear_layout;
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
        setContentView(R.layout.activity_leaderboard);

        point = findViewById(R.id.point);
        points = findViewById(R.id.points);
        points5 = findViewById(R.id.points5);
        leaderboard = findViewById(R.id.Leaderboard);
        downloadingLeaderboard = findViewById(R.id.downloadingLeaderboard);
        button_friends = findViewById(R.id.button_friends);
        button_world = findViewById(R.id.button_world);
       // leaderboardTextView = findViewById(R.id.leaderboardTextView);
        linear_layout = findViewById(R.id.linear_layout);

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferences.getString("nick", "");

        downloading = downloadingLeaderboard.getText().toString();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Nick/" + nick);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                my_points = snapshot.child("points").getValue(Integer.class);;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        adview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        button_world.performClick();
    }

    public void world(View view){
        linear_layout.removeAllViews();

        button_world.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.almost_transparent));
        button_friends.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.transparent));

        databaseReference = FirebaseDatabase.getInstance().getReference("Nick");

        downloadingLeaderboard.setText(downloading);

        Query query = databaseReference.orderByChild("points").startAt(1).limitToLast(10);

        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserLeaderboard> userList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nick = snapshot.child("nick").getValue(String.class);
                    int points = snapshot.child("points").getValue(Integer.class);

                    UserLeaderboard user = new UserLeaderboard(nick, points);
                    userList.add(user);
                }

                Collections.sort(userList, new Comparator<UserLeaderboard>() {
                    @Override
                    public int compare(UserLeaderboard user1, UserLeaderboard user2) {
                        return Integer.compare(user2.getPoints(), user1.getPoints());
                    }
                });

               // StringBuilder leaderboardText = new StringBuilder();
                for (int i = 0; i < userList.size(); i++) {
                    TextView row = new TextView(getApplicationContext());
                    row.setTextSize(24);
                    row.setTextColor(R.color.light_grey);

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
                  //  leaderboardText.append(leaderboardEntry);
                    row.setText(leaderboardEntry);
                    downloadingLeaderboard.setText(leaderboard.getText().toString());
                    linear_layout.addView(row);
                }


               // leaderboardTextView.setTextSize(24);
               // leaderboardTextView.setText(leaderboardText.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                leaderboardTextView.setText("Error " + databaseError.getMessage());
            }
        });
    }


    public void friends(View view) {
        linear_layout.removeAllViews();

        button_world.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.transparent));
        button_friends.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.almost_transparent));

        friendsList = new ArrayList<>();
        friendsLeaderboard = new ArrayList<>();

        downloadingLeaderboard.setText(downloading);

        databaseReference = FirebaseDatabase.getInstance().getReference("Nick/" + nick + "/Friends");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        friendsList.add(childSnapshot.getValue(String.class));
                    }

                    // Po dodaniu wszystkich znajomych do friendsList, możesz kontynuować operacje
                    getFriendsPoints();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Obsługa błędów
            }
        });
    }

    private void getFriendsPoints() {
        if (friendsList.isEmpty()) {
            // Brak znajomych do przetworzenia, można od razu wyświetlić wynik
            showFriendsLeaderboard();
        } else {
            for (String friend : friendsList) {
                DatabaseReference pointsReference = FirebaseDatabase.getInstance().getReference("Nick/" + friend + "/points");
                pointsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendsLeaderboard.add(new UserLeaderboard(friend, snapshot.getValue(Integer.class)));

                        // Sprawdź, czy już pobrano punkty wszystkich znajomych
                        if (friendsLeaderboard.size() == friendsList.size()) {
                            // Wywołaj sortowanie i wyświetlanie po zakończeniu pobierania wszystkich danych
                            showFriendsLeaderboard();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Obsługa błędów
                    }
                });
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    private void showFriendsLeaderboard(){
        friendsLeaderboard.add(new UserLeaderboard(nick, my_points));
        Comparator<UserLeaderboard> comparator = new Comparator<UserLeaderboard>() {
            @Override
            public int compare(UserLeaderboard user1, UserLeaderboard user2) {
                return Integer.compare(user2.getPoints(), user1.getPoints()); // Od najwyższego do najniższego
            }
        };

        Collections.sort(friendsLeaderboard, comparator);

        for (int i = 0; i < friendsLeaderboard.size(); i++) {
            TextView row = new TextView(getApplicationContext());
            row.setTextSize(24);

            String leaderboardEntry = "";
            if(friendsLeaderboard.get(i).getPoints() == 1){
                leaderboardEntry = (i + 1) + ": " + friendsLeaderboard.get(i).getNick() + " - " + friendsLeaderboard.get(i).getPoints() + " "  + point.getText().toString() + "\n\n";
            }
            else if(friendsLeaderboard.get(i).getPoints() > 1 && friendsLeaderboard.get(i).getPoints() < 5){
                leaderboardEntry = (i + 1) + ": " + friendsLeaderboard.get(i).getNick() + " - " + friendsLeaderboard.get(i).getPoints() + " " + points.getText().toString() + "\n\n" ;
            }
            else if(friendsLeaderboard.get(i).getPoints() > 4){
                leaderboardEntry += (i + 1) + ": " + friendsLeaderboard.get(i).getNick() + " - " + friendsLeaderboard.get(i).getPoints() + " " + points5.getText().toString() + "\n\n";
            }

            int textColor = ContextCompat.getColor(getApplicationContext(), R.color.light_grey);
            if (friendsLeaderboard.get(i).getNick().equals(nick)) {
                textColor = ContextCompat.getColor(getApplicationContext(), R.color.green);
            }
            row.setTextColor(textColor);
            row.setText(leaderboardEntry);
            downloadingLeaderboard.setText(leaderboard.getText().toString());
            linear_layout.addView(row);
        }


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