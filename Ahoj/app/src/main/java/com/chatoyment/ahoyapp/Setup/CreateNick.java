package com.chatoyment.ahoyapp.Setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.chatoyment.ahoyapp.MapActivityMain;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.OnlyJava.UserInfo;
import com.chatoyment.ahoyapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CreateNick extends AppCompatActivity implements OnlineDate.OnDateFetchedListener {
    String date_and_time;
    String encryptedText_str;
    EditText nick;
    TextView nick_already_exist, an_error_occurred, nickname_cannot_be_empty, nick_should_not_contain;
    LottieAnimationView loading_animation;
    Long millis;
    Date currentDate;
    int done_counter = 0;

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
        setContentView(R.layout.activity_create_nick);

        OnlineDate.fetchDateAsync(this);

        Intent intent = getIntent();
        date_and_time = intent.getStringExtra("date_and_time");
        encryptedText_str = intent.getStringExtra("encrypted_email");


        nick = findViewById(R.id.nick);
        nick_already_exist = findViewById(R.id.nick_already_exist);
        an_error_occurred = findViewById(R.id.an_error_occurred);
        nickname_cannot_be_empty = findViewById(R.id.nickname_cannot_be_empty);
        nick_should_not_contain = findViewById(R.id.nick_should_not_contain);
        loading_animation = findViewById(R.id.loading_animation);

    }



    public void create(View view){

        if(nick.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), nickname_cannot_be_empty.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(nick.getText().toString().contains(".") || nick.getText().toString().contains("#") || nick.getText().toString().contains("$") || nick.getText().toString().contains("[") || nick.getText().toString().contains("]") || nick.getText().toString().contains(" ")){
            Toast.makeText(getApplicationContext(), nick_should_not_contain.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }



        loading_animation.setVisibility(View.VISIBLE);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Nick");

        DatabaseReference finalReference1 = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.child(nick.getText().toString()).exists()) {
                    loading_animation.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), nick_already_exist.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    currentDate = OnlineDate.getDate();
                    String formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate);

                    UserInfo userInfo = new UserInfo(nick.getText().toString(), encryptedText_str, 0, formattedCurrentDate);

                    finalReference1.child(nick.getText().toString()).setValue(userInfo);
                    done_counter++;
                    loadMapActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading_animation.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), an_error_occurred.getText().toString(), Toast.LENGTH_LONG).show();
                return;
            }
        });




        reference = FirebaseDatabase.getInstance().getReference("UserEmails");

        DatabaseReference finalReference = reference;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(date_and_time).exists()){
                    finalReference.child(date_and_time).child("email").setValue(encryptedText_str);
                    done_counter++;
                    loadMapActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading_animation.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), an_error_occurred.getText().toString(), Toast.LENGTH_LONG).show();
            }


        });

    }

    private void loadMapActivity(){
        if(done_counter == 2){
            SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_nick = sharedPreferences.edit();
            editor_nick.putString("nick", nick.getText().toString());
            editor_nick.apply();

            Intent intent = new Intent(CreateNick.this, MapActivityMain.class);
            intent.putExtra("activity", "user");
            startActivity(intent);
        }
    }

    @Override
    public void onDateFetched(Date date) {
        currentDate = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;
    }
}