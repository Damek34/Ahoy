package com.example.ahoj.Setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ahoj.R;

public class RegisterOrLogin extends AppCompatActivity {

    Intent activity_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_login);

        activity_intent = getIntent();
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
}