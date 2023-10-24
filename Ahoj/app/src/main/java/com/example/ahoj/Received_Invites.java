package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class Received_Invites extends AppCompatActivity {
    GridLayout grid_layout;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    String nick = "";
    ArrayList<String> users;
    TextView accepted, accept, reject, rejected;

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
        setContentView(R.layout.activity_received_invites);

        grid_layout = findViewById(R.id.grid_layout);
        accepted = findViewById(R.id.accepted);
        accept = findViewById(R.id.accept);
        reject = findViewById(R.id.reject);
        rejected = findViewById(R.id.rejected);

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferences.getString("nick", "");

        users = new ArrayList<>();

        reference = database.getReference("Nick/" + nick + "/invites");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnapshot : snapshot.getChildren()){
                        String save_nick = childSnapshot.getValue(String.class);

                        TextView user_nick = new TextView(getApplicationContext());

                        user_nick.setText(save_nick);
                        user_nick.setTextColor(R.color.light_grey);
                        user_nick.setTextSize(18);
                        user_nick.setPadding(0,0,20,0);

                        Button acceptbtn = new Button(getApplicationContext());
                        Button rejectbtn = new Button(getApplicationContext());
                        acceptbtn.setText(accept.getText().toString());
                        acceptbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference.child(save_nick).removeValue();
                                reference = database.getReference("Nick/" + nick + "/Friends");
                               /* reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        reference.child(save_nick).setValue(save_nick);
                                        reference = database.getReference("Nick/" + nick + "/invites");
                                        reference.child(save_nick).removeValue();
                                        grid_layout.removeView(user_nick);
                                        grid_layout.removeView(acceptbtn);
                                        grid_layout.removeView(rejectbtn);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                */
                                reference.child(save_nick).setValue(save_nick);
                                grid_layout.removeView(user_nick);
                                grid_layout.removeView(acceptbtn);
                                grid_layout.removeView(rejectbtn);
                                reference = database.getReference("Nick/" + save_nick + "/Friends");
                                reference.child(nick).setValue(nick);
                                Toast.makeText(getApplicationContext(), accepted.getText().toString(), Toast.LENGTH_SHORT).show();

                            }

                        });


                        rejectbtn.setText(reject.getText().toString());
                        rejectbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference = database.getReference("Nick/" + nick + "/invites/" + save_nick);
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        reference.removeValue();
                                        grid_layout.removeView(user_nick);
                                        grid_layout.removeView(acceptbtn);
                                        grid_layout.removeView(rejectbtn);
                                        Toast.makeText(getApplicationContext(), rejected.getText().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        grid_layout.addView(user_nick);
                        grid_layout.addView(acceptbtn);
                        grid_layout.addView(rejectbtn);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void exit(View view) {
        Intent intent = new Intent(Received_Invites.this, FriendsActivity.class);
        startActivity(intent);
    }
}