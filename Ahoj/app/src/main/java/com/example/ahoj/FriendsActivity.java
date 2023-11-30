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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class FriendsActivity extends AppCompatActivity {

    ScrollView friendsList_scrollview;
    EditText search_user_edittext;
    TextView user_not_found, you_cant_invite_yourself, invitation_sent, invite, remove_friend, done;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    LinearLayout linear_layout_search_friends;
    GridLayout grid_layout_friends;
    String nick;
    SharedPreferences sharedPreferences;

    ArrayList<String> friends;

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
        setContentView(R.layout.activity_friends);
        search_user_edittext = findViewById(R.id.search_user_edittext);
        user_not_found = findViewById(R.id.user_not_found);
        linear_layout_search_friends = findViewById(R.id.linear_layout_search_friends);
        you_cant_invite_yourself = findViewById(R.id.you_cant_invite_yourself);
        invitation_sent = findViewById(R.id.invitation_sent);
        invite = findViewById(R.id.invite);
        friendsList_scrollview = findViewById(R.id.friendsList_scrollview);
        grid_layout_friends = findViewById(R.id.grid_layout_friends);
        remove_friend = findViewById(R.id.remove_friend);
        done = findViewById(R.id.done);

        friends = new ArrayList<>();

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferences.getString("nick", "");

        reference = database.getReference("Nick/" + nick + "/Friends");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnapshot : snapshot.getChildren()){
                        friends.add(childSnapshot.getValue(String.class));

                        TextView nickV = new TextView(getApplicationContext());

                        nickV.setText(childSnapshot.getValue(String.class));
                        nickV.setTextColor(R.color.white);
                        nickV.setTextSize(22);
                        nickV.setPadding(0,0,20,0);

                        Button remove_friend_btn = new Button(getApplicationContext());
                        remove_friend_btn.setText(remove_friend.getText().toString());

                        remove_friend_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference = database.getReference("Nick/" + nick + "/Friends");
                                reference.child(nickV.getText().toString()).removeValue();

                                reference = database.getReference("Nick/" + nickV.getText().toString() + "/Friends");
                                reference.child(nick).removeValue();

                                grid_layout_friends.removeView(nickV);
                                grid_layout_friends.removeView(remove_friend_btn);

                                //przetestować te linijke vvvv
                                friends.remove(childSnapshot.getValue(String.class));

                                Toast.makeText(getApplicationContext(), done.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        grid_layout_friends.addView(nickV);
                        grid_layout_friends.addView(remove_friend_btn);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void exit(View view) {
        Intent intent = new Intent(FriendsActivity.this, MapActivityMain.class);
        intent.putExtra("activity", "user");
        startActivity(intent);
    }

    public void addFriend(View view){
        linear_layout_search_friends.removeAllViews();

        if(nick.equals(search_user_edittext.getText().toString())){
            Toast.makeText(getApplicationContext(), you_cant_invite_yourself.getText().toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        if(search_user_edittext.getText().toString().trim().equals("")){
            return;
        }
        for(String temp : friends){
            if(search_user_edittext.getText().toString().trim().equals(temp)){
                return;
            }
        }

        reference = database.getReference("Nick/" + search_user_edittext.getText().toString());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TextView nickV = new TextView(getApplicationContext());
                    nickV.setText(search_user_edittext.getText().toString());
                    nickV.setTextColor(R.color.light_grey);
                    nickV.setTextSize(18);
                    nickV.setPadding(0,0,20,0);

                    Button addButton = new Button(getApplicationContext());
                    addButton.setText(invite.getText().toString());
                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reference = database.getReference("Nick/" + search_user_edittext.getText().toString() + "/invites");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    reference.child(nick).setValue(nick);
                                    Toast.makeText(getApplicationContext(), invitation_sent.getText().toString(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });

                    linear_layout_search_friends.addView(nickV);
                    linear_layout_search_friends.addView(addButton);
                } else {
                    Toast.makeText(getApplicationContext(), user_not_found.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }

    public void cancelSearch(View view){
        linear_layout_search_friends.removeAllViews();
        search_user_edittext.setText("");
        search_user_edittext.clearFocus();
    }

    public void received(View view){
        startActivity(new Intent(FriendsActivity.this, Received_Invites.class));
    }
}