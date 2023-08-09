package com.example.ahoj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.ahoj.Setup.setup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import DatabaseFiles.CountryAge.CountryAgeDatabase;
import DatabaseFiles.CountryAge.User;

public class LoadingScreen extends AppCompatActivity {

    private DatabaseReference versionRef;
    private String appVersion = "0.91";

    TextView Textview_app_version_is_not_actual, Textview_please_update_application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        Textview_please_update_application = findViewById(R.id.Textview_please_update_application);
        Textview_app_version_is_not_actual = findViewById(R.id.Textview_app_version_is_not_actual);

        versionRef = FirebaseDatabase.getInstance().getReference().child("Version");

        versionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firebaseVersion = dataSnapshot.getValue(String.class);

                    if (!firebaseVersion.equals(appVersion)) {
                        showVersionMismatchDialog();

                    }
                    else{
                        continueLoading();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        }
    //}

    private void showVersionMismatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Textview_app_version_is_not_actual.getText().toString());
        builder.setMessage(Textview_please_update_application.getText().toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(LoadingScreen.this, LoadingScreen.class));
                finish();
            }
        });
        builder.setCancelable(false); // Użytkownik nie może zamknąć dialogu przyciskiem "Back"
        builder.show();
    }

     private void continueLoading(){
        SharedPreferences sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (authToken != null) {
            Intent intent = new Intent(LoadingScreen.this, MapActivityMain.class);
            intent.putExtra("activity", "user");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
            }, 1500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingScreen.this, setup.class));
                    overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
                }
            }, 1500);
        }
    }
}