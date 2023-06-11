package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class Pay extends AppCompatActivity {

    Button exit, pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        exit = findViewById(R.id.buttonExitPay);
        pay = findViewById(R.id.buttonPay);

    }
    public void exitPay(View view){
        startActivity(new Intent(Pay.this, MapActivityMain.class));
    }

    public void pay(View view){


    }
}