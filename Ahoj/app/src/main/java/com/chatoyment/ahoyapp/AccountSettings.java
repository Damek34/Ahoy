package com.chatoyment.ahoyapp;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class AccountSettings extends AppCompatActivity {

    TextView user_nick_textview, nick_textview, user_email_textview, email_textview, textview_done, textview_error, textview_minimum_char;
    boolean is_nick_textview_visible = false, is_email_textview_visible = false;
    SharedPreferences sharedPreferences;
    String nick = "", email = "";
    View nick_view;
    Intent activity_intent;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ConstraintLayout change_password_layout;
    EditText new_password_edittext;
    DatabaseReference reference;
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
        setContentView(R.layout.activity_account_settings);

        activity_intent = getIntent();
        user_nick_textview = findViewById(R.id.user_nick_textview);
        nick_textview = findViewById(R.id.nick_textview);
        nick_view = findViewById(R.id.nick_view);
        user_email_textview = findViewById(R.id.user_email_textview);
        email_textview = findViewById(R.id.email_textview);
        change_password_layout = findViewById(R.id.change_password_layout);
        new_password_edittext = findViewById(R.id.new_password_edittext);
        textview_error = findViewById(R.id.textview_error);
        textview_done = findViewById(R.id.textview_done);
        textview_minimum_char = findViewById(R.id.textview_minimum_char);



        if(activity_intent.getStringExtra("activity").equals("user")){
            sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
            nick = sharedPreferences.getString("nick", "");
            user_nick_textview.setText(nick);

            reference = database.getReference("Nick/" + nick);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        email = EncryptionHelper.decrypt(snapshot.child("email").getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else{
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            nick_textview.setVisibility(View.GONE);
            nick_view.setVisibility(View.GONE);
            email = sharedPreferences.getString("email", "");
        }


        nick_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_nick_textview_visible){
                    user_nick_textview.setVisibility(View.VISIBLE);
                    is_nick_textview_visible = true;
                }
                else{
                    user_nick_textview.setVisibility(View.GONE);
                    is_nick_textview_visible = false;
                }
            }
        });
        email_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_email_textview.setText(email);
                if(!is_email_textview_visible){
                    user_email_textview.setVisibility(View.VISIBLE);
                    is_email_textview_visible = true;
                }
                else{
                    user_email_textview.setVisibility(View.GONE);
                    is_email_textview_visible = false;
                }
            }
        });
    }

    public void changePassword(View view){
        change_password_layout.setVisibility(View.VISIBLE);
    }
    public void cancel(View view){
        change_password_layout.setVisibility(View.GONE);
    }
    public void change(View view){
        if(new_password_edittext.getText().toString().trim().length() < 8){
            Toast.makeText(getApplicationContext(), textview_minimum_char.getText().toString(), Toast.LENGTH_SHORT).show();
        }
        else{
            user.updatePassword(new_password_edittext.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), textview_done.getText().toString(), Toast.LENGTH_SHORT).show();
                                cancel(view);
                            } else {
                                Toast.makeText(getApplicationContext(), textview_error.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    public void exitSettings (View view){
        if(activity_intent.getStringExtra("activity").equals("main")){
            Intent intent_activity = new Intent(AccountSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "main");
            startActivity(intent_activity);
        }
        else{
            Intent intent_activity = new Intent(AccountSettings.this, SettingActivity.class);
            intent_activity.putExtra("activity", "user");
            startActivity(intent_activity);
        }
    }
}