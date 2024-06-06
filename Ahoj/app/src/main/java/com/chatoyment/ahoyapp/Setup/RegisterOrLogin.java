package com.chatoyment.ahoyapp.Setup;

import android.app.Activity;
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


import com.chatoyment.ahoyapp.MapActivityMain;
import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;
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
    TextView an_error_occurred;

    GoogleSignInButton googleBtn;
    GoogleSignInOptions google_options;
    GoogleSignInClient google_client;
    GoogleSignInAccount google_account;

    FirebaseAuth auth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Date date;
    Long millis;
    String date_and_time;




    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    String email = signInAccount.getEmail();


                    String encryptedText_str = EncryptionHelper.encrypt(email);



                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                auth = FirebaseAuth.getInstance();
                                reference = database.getReference("UserEmails");
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.child(date_and_time).exists()){
                                            reference.child(date_and_time).child("email").setValue(encryptedText_str);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                //Toast.makeText(getApplicationContext(), auth.getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), an_error_occurred.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), an_error_occurred.getText().toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                Intent signInIntent = google_client.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
        });


        if(activity_intent.getStringExtra("activity").equals("main")){
            googleBtn.setVisibility(View.GONE);
            textViewOr.setVisibility(View.GONE);
            arrowToBottom.setVisibility(View.GONE);


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

    @Override
    public void onDateFetched(Date date) {
        date = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;
    }
}