package com.example.ahoj.Setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahoj.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    EditText email;
    TextView not_exist, resetlinksent, fail;
    Intent activity_intent;
    String email_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        activity_intent = getIntent();
        email = findViewById(R.id.email);
        not_exist = findViewById(R.id.notexist);
        resetlinksent = findViewById(R.id.resetlinksent);
        fail = findViewById(R.id.fail);
    }

    public void reset(View view){
        email_str = email.getText().toString();
        String modifiedEmail = email_str.replace(".", ",");
        modifiedEmail = modifiedEmail.replace("#", "_");
        modifiedEmail = modifiedEmail.replace("$", "-");
        modifiedEmail = modifiedEmail.replace("[", "(");
        modifiedEmail = modifiedEmail.replace("]", ")");

        if(activity_intent.getStringExtra("activity").equals("user")) {

            reference = database.getReference().child("UserEmails");

            String finalModifiedEmail = modifiedEmail;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child(finalModifiedEmail).exists()) {
                        resetPassword();
                    } else {
                        Toast.makeText(getApplicationContext(), not_exist.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            reference = database.getReference().child("CompanyEmails");

            String finalModifiedEmail = modifiedEmail;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child(finalModifiedEmail).exists()) {
                        resetPassword();
                    } else {
                        Toast.makeText(getApplicationContext(), not_exist.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    void resetPassword(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email_str).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), resetlinksent.getText().toString(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), fail.getText().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void exit(View view){
        if(activity_intent.getStringExtra("activity").equals("user")){
            startActivity(new Intent(ForgotPassword.this, LoginUser.class));
        }
        else{
            startActivity(new Intent(ForgotPassword.this, Login.class));
        }
    }
}