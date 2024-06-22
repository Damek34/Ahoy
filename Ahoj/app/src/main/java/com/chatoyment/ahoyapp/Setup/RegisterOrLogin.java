package com.chatoyment.ahoyapp.Setup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.airbnb.lottie.LottieAnimationView;
import com.chatoyment.ahoyapp.MapActivityMain;
import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;
import com.chatoyment.ahoyapp.Statute;
import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class RegisterOrLogin extends AppCompatActivity implements OnlineDate.OnDateFetchedListener {

    Intent activity_intent;
    TextView textViewOr;
    View arrowToBottom;
    TextView an_error_occurred, this_email_address_is_already_assigned_to_another_account, tos;

    GoogleSignInButton googleBtn;
    GoogleSignInOptions google_options;
    GoogleSignInClient google_client;
    GoogleSignInAccount google_account;

    FirebaseAuth auth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    FirebaseUser user;
    Date date;
    Long millis;
    String date_and_time;
    SharedPreferences sharedPreferences;
    String nick;
    LottieAnimationView loading_animation;


    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(RegisterOrLogin.this);
            if (account != null) {
                google_client.signOut();
            }
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    String email = signInAccount.getEmail();

                    String encryptedText_str = EncryptionHelper.encrypt(email);
                    final String[] email_date_and_time = new String[1];


                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CompanyEmails");

                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                                            String email = companySnapshot.child("email").getValue(String.class);
                                            if (email.equals(encryptedText_str)) {
                                                Toast.makeText(getApplicationContext(), this_email_address_is_already_assigned_to_another_account.getText().toString(), Toast.LENGTH_LONG).show();
                                                loading_animation.setVisibility(View.GONE);
                                                return;
                                            }
                                        }

                                        continueRegister(encryptedText_str, email_date_and_time);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                                //Toast.makeText(getApplicationContext(), auth.getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                            } else {
                                Exception e = task.getException();
                                Toast.makeText(getApplicationContext(), "Blad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                               // Toast.makeText(getApplicationContext(), an_error_occurred.getText().toString(), Toast.LENGTH_SHORT).show();
                                loading_animation.setVisibility(View.GONE);
                            }
                        }
                    });
                } catch (ApiException e) {
                   // Toast.makeText(getApplicationContext(), an_error_occurred.getText().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    loading_animation.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                loading_animation.setVisibility(View.GONE);
            }
        }
    });




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
        setContentView(R.layout.activity_register_or_login);

        activity_intent = getIntent();
        googleBtn = findViewById(R.id.googleBtn);
        textViewOr = findViewById(R.id.textViewOr);
        arrowToBottom = findViewById(R.id.arrowToBottom);
        an_error_occurred = findViewById(R.id.an_error_occurred);
        this_email_address_is_already_assigned_to_another_account = findViewById(R.id.this_email_address_is_already_assigned_to_another_account);
        tos = findViewById(R.id.tos);
        loading_animation = findViewById(R.id.loading_animation);

       // String tekst = EncryptionHelper.decrypt("IsasCfiKv5gxp5rQA0qdzQ/5Svv69XTthEhVP10SshE=");
        //Toast.makeText(getApplicationContext(), tekst, Toast.LENGTH_LONG).show();

        OnlineDate.fetchDateAsync(this);

        date = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;

        FirebaseApp.initializeApp(this);



        google_options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        google_client = GoogleSignIn.getClient(RegisterOrLogin.this, google_options);

        auth = FirebaseAuth.getInstance();

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_animation.setVisibility(View.VISIBLE);
                Intent signInIntent = google_client.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
        });


        if(activity_intent.getStringExtra("activity").equals("main")){
            googleBtn.setVisibility(View.GONE);
            textViewOr.setVisibility(View.GONE);
            arrowToBottom.setVisibility(View.GONE);
            tos.setVisibility(View.GONE);

        }
    }

    public void exit(View view){
        startActivity(new Intent(RegisterOrLogin.this, setup.class));
    }

    public void register(View view){
        if(activity_intent.getStringExtra("activity").equals("main")){
            Intent intent = new Intent(RegisterOrLogin.this, TellSomething.class);
            intent.putExtra("activity", "main");
            startActivity(intent);
        }

        else
        {
            Intent intent = new Intent(RegisterOrLogin.this, RegisterUser.class);
            intent.putExtra("activity", "user");

            startActivity(intent);
        }
    }
    public void login(View view) {
        if (activity_intent.getStringExtra("activity").equals("main")) {
            Intent intent = new Intent(RegisterOrLogin.this, Login.class);
            intent.putExtra("activity", "main");

            startActivity(intent);
        } else {
            Intent intent = new Intent(RegisterOrLogin.this, LoginUser.class);
            intent.putExtra("activity", "user");

            startActivity(intent);
        }
    }


    private void getToken(String nick){
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
                    editor_nick.putString("nick", nick);
                    editor_nick.apply();

                    Intent intent = new Intent(RegisterOrLogin.this, MapActivityMain.class);
                    intent.putExtra("activity", "user");

                    startActivity(intent);
                    loading_animation.setVisibility(View.GONE);

                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Exception exception = task.getException();
                }
            }
        });
    }

    private void continueRegister(String encryptedText_str, String email_date_and_time[]){
        auth = FirebaseAuth.getInstance();
        final DatabaseReference[] reference = {FirebaseDatabase.getInstance().getReference("UserEmails")};
        final boolean[] is_found = {false};
        reference[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (email.equals(encryptedText_str)) {
                        email_date_and_time[0] = userSnapshot.getKey();
                        is_found[0] = true;
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        String emailDB = user.getEmail();

                        reference[0] = FirebaseDatabase.getInstance().getReference("Nick");

                        reference[0].addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot emailSnapshot : snapshot.getChildren()) {
                                    String emailDB = emailSnapshot.child("email").getValue(String.class);
                                    if (encryptedText_str.equals(emailDB)) {
                                        nick = emailSnapshot.getKey();

                                        getToken(nick);
                                       // break;
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                loading_animation.setVisibility(View.GONE);
                            }
                        });

                        //break;
                    }

                }
                if(!is_found[0]){
                    Intent create_nick = new Intent(RegisterOrLogin.this, CreateNick.class);
                    create_nick.putExtra("date_and_time", date_and_time);
                    create_nick.putExtra("encrypted_email", encryptedText_str);


                    startActivity(create_nick);
                    loading_animation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading_animation.setVisibility(View.GONE);
            }
        });
    }

    public void terms(View view){
        Intent intent = new Intent(RegisterOrLogin.this, Statute.class);
        intent.putExtra("activity", "register_or_login");
        if(activity_intent.getStringExtra("activity").equals("main")){
            intent.putExtra("activity_type", "main");
        }
        else{
            intent.putExtra("activity_type", "user");
        }
        startActivity(intent);
    }

    @Override
    public void onDateFetched(Date date) {
        date = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;
    }
}