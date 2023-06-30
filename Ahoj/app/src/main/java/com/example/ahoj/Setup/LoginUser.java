package com.example.ahoj.Setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class LoginUser extends AppCompatActivity {

    EditText nick, password;
    TextView enterNick, enterPassword, success, fail, notVerified, not_exist;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Intent activity_intent;

    String emailDB = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        activity_intent = getIntent();

        mAuth = FirebaseAuth.getInstance();

        nick = findViewById(R.id.nickLogin);
        password = findViewById(R.id.password);
        enterNick = findViewById(R.id.EnterNick);
        enterPassword = findViewById(R.id.EnterPassword);
        success = findViewById(R.id.Success);
        fail = findViewById(R.id.Fail);
        notVerified = findViewById(R.id.notverified);
        not_exist = findViewById(R.id.accnotexist);


    }
    public void exit (View view){
        Intent intent = new Intent(LoginUser.this, RegisterOrLogin.class);
        intent.putExtra("activity", "user");

        startActivity(intent);

    }

    public void login(View view) {
        if (nick.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), enterNick.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if (password.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), enterPassword.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        reference = database.getReference("Nick/" + nick.getText().toString());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    emailDB =  snapshot.child("email").getValue(String.class);
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

    void login(){
        mAuth.signInWithEmailAndPassword(emailDB, password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                       /* Toast.makeText(getApplicationContext(), success.getText().toString(), Toast.LENGTH_LONG).show();
                        String authToken = "";
                        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("auth_token", authToken);
                        editor.apply();

                        */

                        getToken();

                       /* Intent intent = new Intent(LoginUser.this, MapActivityMain.class);
                        intent.putExtra("activity", "user");

                        startActivity(intent);

                        overridePendingTransition(R.layout.fade_in, R.layout.fade_out);

                        */
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

    void getToken(){
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
                    editor_nick.putString("nick", nick.getText().toString());
                    editor_nick.apply();

                    Intent intent = new Intent(LoginUser.this, MapActivityMain.class);
                    intent.putExtra("activity", "user");

                    startActivity(intent);

                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                } else {
                    Exception exception = task.getException();
                }
            }
        });
    }

}