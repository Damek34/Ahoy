package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class EnableLocalization extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_localization);
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 225);
            }
        }
    }

    public void continueL(View view){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(EnableLocalization.this, MapActivityMain.class));
                overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
            }
        }, 1500);
    }
}