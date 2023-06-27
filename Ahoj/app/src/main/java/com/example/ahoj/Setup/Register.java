package com.example.ahoj.Setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ahoj.OnlyJava.Setup.RegisterInfo;
import com.example.ahoj.OnlyJava.Setup.VerifyAccounts;
import com.example.ahoj.OnlyJava.UserInfo;
import com.example.ahoj.R;
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
import java.util.Objects;


public class Register extends AppCompatActivity {

    EditText emailEdit, passwordEdit, repeat_passwordEdit, temporary_name_edit;
    Button register_btn;
    String email = "", password = "", repeat_password = "";

    TextView enterEmail, enterPassword, passwordMinimumChar, passwordsDoNotMatch, accCreated, accCreateFail, verifyLinkSend, verifyLinkSendFail, fillAll, verify, rejected, notyet;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private FirebaseAuth mAuth;

    Toolbar toolbarTemporary, toolbarEmail, toolbarPassword, toolbarRepeat;


    Intent activity_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        if(temporary_name_edit.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), fillAll.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        Integer[] isverified = new Integer[1]; //0 = false, 1 = true, 2 = not verified yet
        isverified[0] = 0;

        reference = database.getReference("VerifiedAccounts");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.hasChild(temporary_name_edit.getText().toString())){
                   DataSnapshot mySnapshot = snapshot.child(temporary_name_edit.getText().toString());
                   isverified[0] = mySnapshot.child("IsVerified").getValue(Integer.class);

                   cont(isverified[0]);

               }
               else{
                   isRejected();
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
    void cont(int val){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), accCreated.getText().toString(), Toast.LENGTH_LONG).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    sendEmailVerification(user);


                    toolbarEmail.setVisibility(View.GONE);
                    toolbarPassword.setVisibility(View.GONE);
                    toolbarRepeat.setVisibility(View.GONE);
                    register_btn.setVisibility(View.GONE);
                    toolbarTemporary.setVisibility(View.GONE);

                    reference = database.getReference("CompanyEmails");

                    RegisterInfo registerInfo = new RegisterInfo(email);

                    String modifiedEmail = email.replace(".", ",");
                    modifiedEmail = modifiedEmail.replace("#", "_");
                    modifiedEmail = modifiedEmail.replace("$", "-");
                    modifiedEmail = modifiedEmail.replace("[", "(");
                    modifiedEmail = modifiedEmail.replace("]", ")");

                    reference.child(modifiedEmail).setValue(registerInfo);
                    verify.setVisibility(View.VISIBLE);

                    clearDB();

                }
                else{
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
}