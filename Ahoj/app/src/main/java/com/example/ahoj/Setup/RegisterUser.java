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

public class RegisterUser extends AppCompatActivity {

    EditText emailEdit, passwordEdit, repeat_passwordEdit, nick;
    Button register_btn;
    String email = "", password = "", repeat_password = "";

    Toolbar toolbarNick, toolbarEmail, toolbarPassword, toolbarRepeat;

    TextView enterEmail, enterPassword, passwordMinimumChar, passwordsDoNotMatch, accCreated, accCreateFail, verifyLinkSend, verifyLinkSendFail, fillAll, verify, already_exist;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    public void exit(View view){
        Intent intent = new Intent(RegisterUser.this, RegisterOrLogin.class);
        intent.putExtra("activity", "user");
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


                                toolbarEmail.setVisibility(View.GONE);
                                toolbarPassword.setVisibility(View.GONE);
                                toolbarRepeat.setVisibility(View.GONE);
                                register_btn.setVisibility(View.GONE);
                                toolbarNick.setVisibility(View.GONE);


                                verify.setVisibility(View.VISIBLE);

                                reference = database.getReference("Nick");

                                UserInfo userInfo = new UserInfo(nick.getText().toString(), email, 0);

                                reference.child(nick.getText().toString()).setValue(userInfo);



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

}