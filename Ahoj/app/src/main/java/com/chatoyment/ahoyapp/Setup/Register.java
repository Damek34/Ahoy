package com.chatoyment.ahoyapp.Setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
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

import java.util.Date;
import java.util.Locale;


public class Register extends AppCompatActivity implements OnlineDate.OnDateFetchedListener{

    EditText emailEdit, passwordEdit, repeat_passwordEdit, temporary_name_edit;
    Button register_btn;
    String email = "", password = "", repeat_password = "";

    TextView enterEmail, enterPassword, passwordMinimumChar, passwordsDoNotMatch, accCreated, accCreateFail, verifyLinkSend, verifyLinkSendFail, fillAll, verify, rejected, notyet
            , tos, this_email_address_is_already_assigned_to_another_account;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private FirebaseAuth mAuth;

    Toolbar toolbarTemporary, toolbarEmail, toolbarPassword, toolbarRepeat;

    Date date;
    Long millis;
    String date_and_time;
    Intent activity_intent;

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
        setContentView(R.layout.activity_register);

        activity_intent = getIntent();


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
        rejected = findViewById(R.id.rejected);
        notyet = findViewById(R.id.notyet);
        toolbarTemporary = findViewById(R.id.toolbarTemporaryName);
        toolbarEmail = findViewById(R.id.toolbarEmail);
        toolbarPassword = findViewById(R.id.toolbarPassword);
        toolbarRepeat = findViewById(R.id.toolbarRepeat);
        tos = findViewById(R.id.tos);
        this_email_address_is_already_assigned_to_another_account = findViewById(R.id.this_email_address_is_already_assigned_to_another_account);


        OnlineDate.fetchDateAsync(this);

        date = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;
    }

    public void exit(View view){
        Intent intent = new Intent(Register.this, RegisterOrLogin.class);
        intent.putExtra("activity", "main");

        startActivity(intent);

    }

    public void register(View view){
        emailEdit = findViewById(R.id.Email);
        passwordEdit = findViewById(R.id.password);
        repeat_passwordEdit = findViewById(R.id.repeat);
        temporary_name_edit = findViewById(R.id.temporaryName);

        email = emailEdit.getText().toString().trim();
        password = passwordEdit.getText().toString();
        repeat_password = repeat_passwordEdit.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(getApplicationContext(), enterEmail.getText().toString(), Toast.LENGTH_LONG).show();
            Log.d("RegisterActivity", "Email is empty");
            return;
        }
        if(password.isEmpty()){
            Toast.makeText(getApplicationContext(), enterPassword.getText().toString(), Toast.LENGTH_LONG).show();
            Log.d("RegisterActivity", "Password is empty");
            return;
        }
        if(password.length() < 8){
            Toast.makeText(getApplicationContext(), passwordMinimumChar.getText().toString(), Toast.LENGTH_LONG).show();
            Log.d("RegisterActivity", "Password is less than 8 characters");
            return;
        }
        if(!repeat_password.equals(password)){
            Toast.makeText(getApplicationContext(), passwordsDoNotMatch.getText().toString(), Toast.LENGTH_LONG).show();
            Log.d("RegisterActivity", "Passwords do not match");
            return;
        }
        if(temporary_name_edit.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), fillAll.getText().toString(), Toast.LENGTH_LONG).show();
            Log.d("RegisterActivity", "Temporary name is empty");
            return;
        }

        String encrypted_email = EncryptionHelper.encrypt(email);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserEmails");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean emailExists = false;
                for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                    String email = companySnapshot.child("email").getValue(String.class);
                    if (email.equals(encrypted_email)) {
                        emailExists = true;
                        Toast.makeText(getApplicationContext(), this_email_address_is_already_assigned_to_another_account.getText().toString(), Toast.LENGTH_LONG).show();
                        Log.d("RegisterActivity", "Email already exists in database");
                        break;
                    }
                }
                if (!emailExists) {
                    Log.d("RegisterActivity", "Email not found in database, proceeding with registration");
                    continueRegister();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), accCreateFail.getText().toString(), Toast.LENGTH_LONG).show();
                Log.d("RegisterActivity", "Database error: " + error.getMessage());
            }
        });
    }


    private void continueRegister(){
        Integer[] isverified = new Integer[1]; //0 = false, 1 = true, 2 = not verified yet
        isverified[0] = 0;

        reference = database.getReference("VerifiedAccounts");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(temporary_name_edit.getText().toString())){
                    DataSnapshot mySnapshot = snapshot.child(temporary_name_edit.getText().toString());
                    isverified[0] = mySnapshot.child("IsVerified").getValue(Integer.class);
                    Log.d("RegisterActivity", "User verification status: " + isverified[0]);
                    cont(isverified[0]);
                }
                else{
                    Log.d("RegisterActivity", "User not verified");
                    isRejected();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("RegisterActivity", "Database error: " + error.getMessage());
            }
        });
    }


    public void terms(View view){
        Intent intent = new Intent(Register.this, Statute.class);
        intent.putExtra("activity", "register");
        startActivity(intent);
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
    void cont(int val){
        Log.d("RegisterActivity", "In cont() method, value: " + val);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("RegisterActivity", "User created successfully");
                    Toast.makeText(getApplicationContext(), accCreated.getText().toString(), Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    sendEmailVerification(user);

                    String encryptedText_str = EncryptionHelper.encrypt(email);

                    toolbarEmail.setVisibility(View.GONE);
                    toolbarPassword.setVisibility(View.GONE);
                    toolbarRepeat.setVisibility(View.GONE);
                    register_btn.setVisibility(View.GONE);
                    toolbarTemporary.setVisibility(View.GONE);
                    tos.setVisibility(View.GONE);

                    reference = database.getReference("CompanyEmails");

                    RegisterInfo registerInfo = new RegisterInfo(email);
                    reference.child(date_and_time).child("email").setValue(encryptedText_str);
                    verify.setVisibility(View.VISIBLE);

                    clearDB();

                } else {
                    Log.d("RegisterActivity", "User creation failed: " + task.getException().getMessage());
                    Toast.makeText(getApplicationContext(), accCreateFail.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    void isRejected(){
        reference = database.getReference("AccountsToVerify");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(temporary_name_edit.getText().toString())){
                    notYet();
                    return;
                }
                else{
                    Rejected();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void Rejected(){
        Toast.makeText(getApplicationContext(), rejected.getText().toString(), Toast.LENGTH_LONG).show();
    }

    void notYet(){
        Toast.makeText(getApplicationContext(), notyet.getText().toString(), Toast.LENGTH_LONG).show();

    }

    void clearDB(){
        reference = FirebaseDatabase.getInstance().getReference("VerifiedAccounts/").child(temporary_name_edit.getText().toString());
        reference.removeValue();
    }

    @Override
    public void onDateFetched(Date date) {
        date = OnlineDate.getDate();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;
    }
}