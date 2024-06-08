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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Login extends AppCompatActivity {

    EditText email, password;
    TextView enterEmail, enterPassword, success, fail, notVerified, not_exist, account_is_banned, we_sent_you_new_activation_link;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Intent activity_intent;

    private SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_login);

        activity_intent = getIntent();

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        enterEmail = findViewById(R.id.EnterEmail);
        enterPassword = findViewById(R.id.EnterPassword);
        success = findViewById(R.id.Success);
        fail = findViewById(R.id.Fail);
        notVerified = findViewById(R.id.notverified);
        not_exist = findViewById(R.id.accnotexist);
        account_is_banned = findViewById(R.id.account_is_banned);
        we_sent_you_new_activation_link = findViewById(R.id.we_sent_you_new_activation_link);
        loading_animation = findViewById(R.id.loading_animation);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        email.addTextChangedListener(new TextWatcher() {
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

    public void login(View view) {
        if (email.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), enterEmail.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if (password.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), enterPassword.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        loading_animation.setVisibility(View.VISIBLE);
        String email_str = email.getText().toString();
        String encryption_email = EncryptionHelper.encrypt(email_str);
        final boolean[] account_exist = {false};

        String modifiedEmail = email_str.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");

        reference = database.getReference().child("CompanyEmails");

        String finalModifiedEmail = modifiedEmail;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*if (snapshot.exists() && snapshot.child(finalModifiedEmail).exists()) {
                    login();
                } else {
                    loading_animation.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), not_exist.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
            }

                 */
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);

                    if (encryption_email.equals(email)) {
                        account_exist[0] = true;
                        login();
                    }
                    }
                if(!account_exist[0]){
                    loading_animation.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), not_exist.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void exit (View view){
        Intent intent = new Intent(Login.this, RegisterOrLogin.class);
        intent.putExtra("activity", "main");

        startActivity(intent);

    }
    void login(){

      /*  String modifiedEmail = email.getText().toString().replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");

       */

        String encryptedEmail = EncryptionHelper.encrypt(email.getText().toString());
        final String[] email_date_and_time = new String[1];
        final DatabaseReference[] reference = {FirebaseDatabase.getInstance().getReference("CompanyEmails")};

        reference[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                    String email = companySnapshot.child("email").getValue(String.class);
                    if (email.equals(encryptedEmail)) {
                        email_date_and_time[0] = companySnapshot.getKey();

                        reference[0] = database.getReference("CompanyEmails/" + email_date_and_time[0] + "/is_banned");

                        reference[0].addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    loading_animation.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), account_is_banned.getText().toString(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                else{
                                    countinue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    void countinue(){
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){

                        Toast.makeText(getApplicationContext(), success.getText().toString(), Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email.getText().toString());
                        editor.apply();

                        Intent intent = new Intent(Login.this, MapActivityMain.class);
                        intent.putExtra("activity", "main");

                        startActivity(intent);

                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    else{
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendEmailVerification(user);

                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), notVerified.getText().toString(), Toast.LENGTH_LONG).show();
                        loading_animation.setVisibility(View.GONE);

                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), fail.getText().toString(), Toast.LENGTH_LONG).show();
                    loading_animation.setVisibility(View.GONE);
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

    public void forgotPassword(View view){
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        intent.putExtra("activity", "main");

        startActivity(intent);
    }
}