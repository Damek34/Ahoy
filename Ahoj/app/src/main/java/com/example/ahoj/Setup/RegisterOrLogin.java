package com.example.ahoj.Setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ahoj.R;

public class RegisterOrLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_or_login);
    }

    public void exit(View view){
        startActivity(new Intent(RegisterOrLogin.this, setup.class));
        overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
    }

    public void register(View view){
        startActivity(new Intent(RegisterOrLogin.this, Register.class));
        overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
    }
}