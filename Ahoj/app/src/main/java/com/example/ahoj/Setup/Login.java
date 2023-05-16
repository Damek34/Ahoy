package com.example.ahoj.Setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahoj.MapActivityMain;
import com.example.ahoj.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText email, password;
    TextView enterEmail, enterPassword, success, fail, notVerified;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        enterEmail = findViewById(R.id.EnterEmail);
        enterPassword = findViewById(R.id.EnterPassword);
        success = findViewById(R.id.Success);
        fail = findViewById(R.id.Fail);
        notVerified = findViewById(R.id.notverified);
    }

    public void login(View view){
        if(email.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), enterEmail.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(password.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), enterPassword.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        Toast.makeText(getApplicationContext(), success.getText().toString(), Toast.LENGTH_LONG).show();

                        startActivity(new Intent(Login.this, MapActivityMain.class));
                        overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                    }
                    else{
                        mAuth.signOut();
                        Toast.makeText(getApplicationContext(), notVerified.getText().toString(), Toast.LENGTH_LONG).show();

                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), fail.getText().toString(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void exit(View view){
        startActivity(new Intent(Login.this, RegisterOrLogin.class));

    }
}