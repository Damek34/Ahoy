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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText email, password;
    TextView enterEmail, enterPassword, success, fail, notVerified, not_exist;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Intent activity_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        String email_str = email.getText().toString();

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
                if (snapshot.exists() && snapshot.child(finalModifiedEmail).exists()) {
                    login();
                } else {
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
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        Toast.makeText(getApplicationContext(), success.getText().toString(), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Login.this, MapActivityMain.class);
                        intent.putExtra("activity", "main");

                        startActivity(intent);

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

    public void forgotPassword(View view){
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        intent.putExtra("activity", "main");

        startActivity(intent);
    }
}