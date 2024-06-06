package com.chatoyment.ahoyapp.Setup;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;
import com.example.ahoyapp.OnlyJava.Setup.RegisterInfo;
import com.example.ahoyapp.OnlyJava.UserInfo;
import com.chatoyment.ahoyapp.Statute;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegisterUser extends AppCompatActivity implements OnlineDate.OnDateFetchedListener{

    EditText emailEdit, passwordEdit, repeat_passwordEdit, nick;
    Button register_btn;
    String email = "", password = "", repeat_password = "", nickStr = "";

    Toolbar toolbarNick, toolbarEmail, toolbarPassword, toolbarRepeat;

    TextView enterEmail, enterPassword, passwordMinimumChar, passwordsDoNotMatch, accCreated, accCreateFail, verifyLinkSend, verifyLinkSendFail, fillAll, verify, already_exist, should_not, tos;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private FirebaseAuth mAuth;

    Date date;
    Long millis;
    String date_and_time;

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
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        register_btn = findViewById(R.id.registerBtn);

        enterEmail = findViewById(R.id.EnterEmail);
        enterPassword = findViewById(R.id.EnterPassword);
        passwordMinimumChar = findViewById(R.id.PasswordMinimumChar);
        passwordsDoNotMatch = findViewById(R.id.PasswordDoNotMatch);
        accCreated = findViewById(R.id.AccCreated);
        accCreateFail = findViewById(R.id.AccCreateFail);
        verifyLinkSend = findViewById(R.id.VerifyLinkSend);
        verifyLinkSendFail = findViewById(R.id.VerifyLinkSendFail);
        fillAll = findViewById(R.id.FillAll);
        verify = findViewById(R.id.verifyEmail);
        already_exist = findViewById(R.id.alreadyexist);

        toolbarEmail = findViewById(R.id.toolbarEmail);
        toolbarNick = findViewById(R.id.toolbarNick);
        toolbarPassword = findViewById(R.id.toolbarPassword);
        toolbarRepeat = findViewById(R.id.toolbarRepeat);

        should_not = findViewById(R.id.shouldnot);
        tos = findViewById(R.id.tos);

        OnlineDate.fetchDateAsync(this);
        date = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;


    }

    public void exit(View view){
        Intent intent = new Intent(RegisterUser.this, RegisterOrLogin.class);
        intent.putExtra("activity", "user");
        startActivity(intent);
    }

    public void terms(View view){
        Intent intent = new Intent(RegisterUser.this, Statute.class);
        intent.putExtra("activity", "registerUser");
        startActivity(intent);
    }
    public void register(View view){
        emailEdit = findViewById(R.id.Email);
        passwordEdit = findViewById(R.id.password);
        repeat_passwordEdit = findViewById(R.id.repeat);
        nick = findViewById(R.id.nick);

        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();
        repeat_password = repeat_passwordEdit.getText().toString();

        if(email.trim().equals("")){
            Toast.makeText(getApplicationContext(), enterEmail.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(password.trim().equals("")){
            Toast.makeText(getApplicationContext(), enterPassword.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length() < 8){
            Toast.makeText(getApplicationContext(), passwordMinimumChar.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(!repeat_password.equals(password)){
            Toast.makeText(getApplicationContext(), passwordsDoNotMatch.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(nick.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), fillAll.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }
        if(nickStr.contains(".") || nickStr.contains("#") || nickStr.contains("$") || nickStr.contains("[") || nickStr.contains("]")){
            Toast.makeText(getApplicationContext(), should_not.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        reference = database.getReference().child("Nick");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child(nick.getText().toString()).exists()) {
                    Toast.makeText(getApplicationContext(), already_exist.getText().toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), accCreated.getText().toString(), Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                sendEmailVerification(user);

                                String encryptedText_str = EncryptionHelper.encrypt(email);


                                toolbarEmail.setVisibility(View.GONE);
                                toolbarPassword.setVisibility(View.GONE);
                                toolbarRepeat.setVisibility(View.GONE);
                                register_btn.setVisibility(View.GONE);
                                toolbarNick.setVisibility(View.GONE);


                                verify.setVisibility(View.VISIBLE);

                                reference = database.getReference("UserEmails");

                                RegisterInfo registerInfo = new RegisterInfo(email);
/*
                                String modifiedEmail = email.replace(".", ",");
                                modifiedEmail = modifiedEmail.replace("#", "_");
                                modifiedEmail = modifiedEmail.replace("$", "-");
                                modifiedEmail = modifiedEmail.replace("[", "(");
                                modifiedEmail = modifiedEmail.replace("]", ")");


 */
                               // reference.child(modifiedEmail).setValue(encryptedText_str);
                                reference.child(date_and_time).child("email").setValue(encryptedText_str);



                                Date currentDate = Calendar.getInstance().getTime();
                                String formattedCurrentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate);

                                reference = database.getReference("Nick");

                               // UserInfo userInfo = new UserInfo(nick.getText().toString(), email, 0, formattedCurrentDate);
                                UserInfo userInfo = new UserInfo(nick.getText().toString(), encryptedText_str, 0, formattedCurrentDate);

                                reference.child(nick.getText().toString()).setValue(userInfo);

                                tos.setVisibility(View.GONE);


                            }
                            else{
                                Toast.makeText(getApplicationContext(), accCreateFail.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
    });
    }
    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), verifyLinkSend.getText().toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), verifyLinkSendFail.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDateFetched(Date date) {
        date = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;
    }
}