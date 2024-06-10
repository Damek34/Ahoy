package com.chatoyment.ahoyapp.Setup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.airbnb.lottie.LottieAnimationView;
import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.R;
import com.chatoyment.ahoyapp.MapActivityMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class LoginUser extends AppCompatActivity {

    EditText nick, password;
    TextView enterNick, enterPassword, success, fail, notVerified, not_exist, we_sent_you_new_activation_link;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Intent activity_intent;

    String emailDB = "";

    LottieAnimationView loading_animation;

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
        setContentView(R.layout.activity_login_user);

        activity_intent = getIntent();

        mAuth = FirebaseAuth.getInstance();

        nick = findViewById(R.id.nickLogin);
        password = findViewById(R.id.password);
        enterNick = findViewById(R.id.EnterNick);
        enterPassword = findViewById(R.id.EnterPassword);
        success = findViewById(R.id.Success);
        fail = findViewById(R.id.Fail);
        notVerified = findViewById(R.id.notverified);
        not_exist = findViewById(R.id.accnotexist);
        we_sent_you_new_activation_link = findViewById(R.id.we_sent_you_new_activation_link);
        loading_animation = findViewById(R.id.loading_animation);


        nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                we_sent_you_new_activation_link.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    public void exit (View view){
        Intent intent = new Intent(LoginUser.this, RegisterOrLogin.class);
        intent.putExtra("activity", "user");

        startActivity(intent);

    }

    public void login(View view) {
        if (nick.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), enterNick.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if (password.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), enterPassword.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        loading_animation.setVisibility(View.VISIBLE);

        if (nick.getText().toString().contains(".") || nick.getText().toString().contains("#") || nick.getText().toString().contains("$") || nick.getText().toString().contains("[") || nick.getText().toString().contains("]") || nick.getText().toString().contains(" ")) {
            Toast.makeText(getApplicationContext(), not_exist.getText().toString(), Toast.LENGTH_LONG).show();
            loading_animation.setVisibility(View.GONE);
            return;
        }

        reference = database.getReference("Nick/" + nick.getText().toString());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    emailDB =  snapshot.child("email").getValue(String.class);
                    login();
                } else {
                   Toast.makeText(getApplicationContext(), not_exist.getText().toString(), Toast.LENGTH_LONG).show();
                    loading_animation.setVisibility(View.GONE);

                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void login(){
        mAuth.signInWithEmailAndPassword(EncryptionHelper.decrypt(emailDB), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        getToken();
                    }
                    else{
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendEmailVerification(user);

                        mAuth.signOut();

                        loading_animation.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), notVerified.getText().toString(), Toast.LENGTH_LONG).show();

                    }

                }
                else{
                    loading_animation.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), fail.getText().toString(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        we_sent_you_new_activation_link.setVisibility(View.VISIBLE);
                    } else {
                        //   Toast.makeText(getApplicationContext(), verifyLinkSendFail.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    void getToken(){
        user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    String authToken = task.getResult().getToken();

                    SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("auth_token", authToken);
                    editor.apply();

                    SharedPreferences.Editor editor_nick = sharedPreferences.edit();
                    editor_nick.putString("nick", nick.getText().toString());
                    editor_nick.apply();

                    Intent intent = new Intent(LoginUser.this, MapActivityMain.class);
                    intent.putExtra("activity", "user");

                    startActivity(intent);

                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Exception exception = task.getException();
                }
            }
        });
    }


    public void forgotPassword(View view){
        Intent intent = new Intent(LoginUser.this, ForgotPassword.class);
        intent.putExtra("activity", "user");

        startActivity(intent);
    }

}