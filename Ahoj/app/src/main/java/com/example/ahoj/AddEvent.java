package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Visibility;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddEvent extends AppCompatActivity {

    int page = 1;
    String nameV, descV, locationV, company_nameV;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


        TextView name = (TextView) findViewById(R.id.textViewEventName);
        TextView description = (TextView) findViewById(R.id.textViewEventDescription);
        TextView location = (TextView) findViewById(R.id.event_location);
        TextView company_name = (TextView) findViewById(R.id.textViewEventCompany);

        EditText editName = (EditText) findViewById(R.id.event_name);
        EditText editDescription = (EditText) findViewById(R.id.event_description);


        name.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        editName.setVisibility(View.VISIBLE);
        editDescription.setVisibility(View.VISIBLE);


    }

    public void exitAdd(View view) {
        startActivity(new Intent(AddEvent.this, MapActivityMain.class));
    }

    public void addEvent(View view) {

        // Map<String, Object> eventValues = new HashMap<>();
        //   eventValues.put("EventName", "Urodziny traweczki");
        //  eventValues.put("EventDescription", "Wielka libacja");
        //  eventValues.put("EventCompanyName", "PowerStudio");
        //  eventValues.put("EventLocalization", "Sanok");
        boolean canBeAdded = true;


        EditText editName = (EditText) findViewById(R.id.event_name);
        EditText description = (EditText) findViewById(R.id.event_description);
        EditText location = (EditText) findViewById(R.id.event_location);
        EditText company_name = (EditText) findViewById(R.id.event_company_name);
        nameV = editName.getText().toString();
        descV = description.getText().toString();
        locationV = location.getText().toString();
        company_nameV = company_name.getText().toString();

        if (nameV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi mieć nazwę!", Toast.LENGTH_LONG).show();
        }
        if (descV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi mieć opis!", Toast.LENGTH_LONG).show();
        }
        if (locationV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi mieć lokalizację!", Toast.LENGTH_LONG).show();
        }
        if (company_nameV.trim().isEmpty()) {
            canBeAdded = false;
            Toast.makeText(this, "Event musi zawierać nazwę firmy!", Toast.LENGTH_LONG).show();
        }

        if (canBeAdded) {
            Date date = new Date();
            long millis = System.currentTimeMillis();
            String date_and_time = date + " " + millis;

            reference = database.getReference("Event");
            // reference.setValue(date_and_time);
            AddEventInfo newEvent = new AddEventInfo(date_and_time, nameV, descV, locationV, company_nameV);
            reference.child(date_and_time).setValue(newEvent);

            Toast.makeText(this, "dodano", Toast.LENGTH_LONG).show();
            startActivity(new Intent(AddEvent.this, MapActivityMain.class));
        }




    }
}