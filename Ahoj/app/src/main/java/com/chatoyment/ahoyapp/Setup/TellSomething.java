package com.chatoyment.ahoyapp.Setup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.chatoyment.ahoyapp.R;
import com.example.ahoyapp.OnlyJava.Setup.VerifyAccounts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class TellSomething extends AppCompatActivity {

    EditText nick, tell;
    TextView please_fill, choose_other, should_not;
    Toolbar wait, nickT, tellT;

    Button btn_send;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        String savedLanguage = sharedPreferences2.getString("selectedLanguage", null);

        if (savedLanguage.equals("en")) {
            Locale locale2 = new Locale("en");
            Locale.setDefault(locale2);
            Configuration config = new Configuration();
            config.locale = locale2;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            Locale myLocale = new Locale("en");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
        else if (savedLanguage.equals("pl")) {
            Locale locale2 = new Locale("pl");
            Locale.setDefault(locale2);
            Configuration config = new Configuration();
            config.locale = locale2;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            Locale myLocale = new Locale("pl");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tell_something);

        nick = findViewById(R.id.temporaryNick);
        tell = findViewById(R.id.writeSomething);
        please_fill = findViewById(R.id.pleasefill);
        choose_other = findViewById(R.id.chooseother);
        wait = findViewById(R.id.wait);
        should_not = findViewById(R.id.shouldnot);
        btn_send = findViewById(R.id.sendInfo);
        nickT = findViewById(R.id.toolbarTemporaryName);
        tellT = findViewById(R.id.toolbarWriteSomething);
    }

    public void exitTell(View view){
        Intent intent = new Intent(TellSomething.this, RegisterOrLogin.class);
        intent.putExtra("activity", "main");

        startActivity(intent);

    }

    public void sendInfo(View view){
        if(nick.getText().toString().trim().isEmpty() || tell.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), please_fill.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        String nickStr = nick.getText().toString();

       if(nickStr.contains(".") || nickStr.contains("#") || nickStr.contains("$") || nickStr.contains("[") || nickStr.contains("]")){
           Toast.makeText(getApplicationContext(), should_not.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }


        final boolean[] can_be_added = {true};

         String[] nickDB = {""};
        reference = database.getReference("AccountsToVerify");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(nick.getText().toString())){
                Toast.makeText(getApplicationContext(), choose_other.getText().toString(), Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), "testowy zez", Toast.LENGTH_LONG).show();
                can_be_added[0] = false;
                return;
                }
                else{
                    addToDB();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void addToDB(){
        reference = database.getReference("AccountsToVerify");
        VerifyAccounts verifyAccounts = new VerifyAccounts(nick.getText().toString(), tell.getText().toString());
        reference.child(nick.getText().toString()).setValue(verifyAccounts);
        wait.setVisibility(View.VISIBLE);
        nick.setVisibility(View.GONE);
        tell.setVisibility(View.GONE);
        btn_send.setVisibility(View.GONE);
        nickT.setVisibility(View.GONE);
        tellT.setVisibility(View.GONE);

    }


    public void alreadyHave(View view){
        startActivity(new Intent(TellSomething.this, Register.class));
    }
}