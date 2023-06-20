package com.example.ahoj.Setup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ahoj.OnlyJava.Setup.VerifyAccounts;
import com.example.ahoj.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class TellSomething extends AppCompatActivity {

    EditText nick, tell;
    TextView please_fill, choose_other, should_not;
    Toolbar wait, nickT, tellT;

    Button btn_send;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        startActivity(new Intent(TellSomething.this, RegisterOrLogin.class));

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