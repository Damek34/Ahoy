package com.example.ahoj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ahoj.OnlyJava.AddEventInfo;
import com.example.ahoj.OnlyJava.CompanyEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventLocalizationPreview extends AppCompatActivity {

    String event_name, location, desc, company_name, additional, duration, countryName, event_will_ends_str, isSocial;
    Spinner countrySpinner;

    Toolbar toolbar;

    TextView textviewCountry, add_announcement, check_internet_connection, event_will_ends, check, location_not_found;

    Button btnokCountry;

    LinearLayout map;

    GoogleMap mMap;
    Marker marker;
    List<Address> addressList;
    Address address;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Date date;
    Long millis;
    String date_and_time;
    Calendar calendar;

    Intent social_intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_localization_previev);

        social_intent = getIntent();

        toolbar = findViewById(R.id.toolbarPreview);
        textviewCountry = findViewById(R.id.textViewEventCountry);
        btnokCountry = findViewById(R.id.buttonOkCountry);
        event_will_ends = findViewById(R.id.event_will_ends);
        check = findViewById(R.id.check);
        location_not_found = findViewById(R.id.location_not_found);

        event_will_ends_str = event_will_ends.getText().toString();

        isSocial = social_intent.getStringExtra("isSocial");



        add_announcement = findViewById(R.id.add_announcementPreview);

        map = findViewById(R.id.mapPreview);

        Intent intent = getIntent();

        event_name = intent.getStringExtra("event_name");
        location = intent.getStringExtra("localization");
        desc = intent.getStringExtra("event_desc");
        company_name = intent.getStringExtra("company_name");
        duration = intent.getStringExtra("duration");
        additional = intent.getStringExtra("additional");

        countrySpinner = (Spinner) findViewById(R.id.eventCountry);

        check_internet_connection = findViewById(R.id.check_internet_connection);

        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();

        for(Locale locale: locales){
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, countries);

        countrySpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.custom_spinner);

        date = new Date();
        millis = System.currentTimeMillis();
        date_and_time = date + " " + millis;


        calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, Integer.parseInt(duration));
        //calendar.add(Calendar.SECOND, Integer.parseInt(duration));

        if(calendar.get(Calendar.MINUTE) < 10){
            event_will_ends.setText(event_will_ends_str + ": "  + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
                    + ", " + calendar.get(Calendar.HOUR) + ":" + "0" + calendar.get(Calendar.MINUTE));
        }
        else{
            event_will_ends.setText(event_will_ends_str + ": "  + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR)
                    + ", " + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));
        }



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPreviewFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

            }
        });

    }

    public void cancel(View view){
        Intent intent = new Intent(EventLocalizationPreview.this, AddLocalEvent.class);

        intent.putExtra("event_name", event_name);
        intent.putExtra("event_desc", desc);
        intent.putExtra("localization", location);
        intent.putExtra("company_name", company_name);
        intent.putExtra("duration", duration);
        intent.putExtra("additional", additional);

        if(isSocial.equals("true")){
            intent.putExtra("isSocial", "true");
        }

        startActivity(intent);
    }


    public void okCountry(View view){
        countryName = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(countrySpinner.getSelectedItem().toString(), 1);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), check_internet_connection.getText().toString(), Toast.LENGTH_LONG).show();
        }

        if(addresses == null){
            return;
        }

        if (addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }



        try {
            addressList = geocoder.getFromLocationName(location, 1);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            address = addressList.get(0);
        }
        catch(IndexOutOfBoundsException e){
            Intent intent = new Intent(EventLocalizationPreview.this, AddLocalEvent.class);

            intent.putExtra("event_name", event_name);
            intent.putExtra("event_desc", desc);
            intent.putExtra("localization", location);
            intent.putExtra("company_name", company_name);
            intent.putExtra("duration", duration);
            intent.putExtra("additional", additional);

            if(isSocial.equals("true")){
                intent.putExtra("isSocial", "true");
            }

            startActivity(intent);

            Toast.makeText(getApplicationContext(), location_not_found.getText().toString(), Toast.LENGTH_LONG).show();

            return;
        }
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        map.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        check.setVisibility(View.VISIBLE);
        countrySpinner.setVisibility(View.GONE);
        textviewCountry.setVisibility(View.GONE);
        btnokCountry.setVisibility(View.GONE);
        event_will_ends.setVisibility(View.GONE);

        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(event_name)).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ahoylocalpin)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 1500, null);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void addEvent(View view){
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");


        if(isSocial.equals("false")){
            reference = database.getReference("Waiting");


            AddEventInfo newEvent = new AddEventInfo(date_and_time, event_name, desc, location, company_name, calendar.getTime(), additional, countryName, savedEmail);
            reference.child(date_and_time).setValue(newEvent);

            Toast.makeText(this, add_announcement.getText().toString(), Toast.LENGTH_LONG).show();


            String modifiedEmail = savedEmail.replace(".", ",");
            modifiedEmail = modifiedEmail.replace("#", "_");
            modifiedEmail = modifiedEmail.replace("$", "-");
            modifiedEmail = modifiedEmail.replace("[", "(");
            modifiedEmail = modifiedEmail.replace("]", ")");



            reference = database.getReference("CompanyEmails/" + modifiedEmail);

            CompanyEvent companyEvent = new CompanyEvent(date_and_time, calendar.getTime(), countryName);
            reference.child("CompanyEvent").setValue(companyEvent);
        }
        else{
            reference = database.getReference("WaitingSocialEvents");


            AddEventInfo newEvent = new AddEventInfo(date_and_time, event_name, desc, location, company_name, calendar.getTime(), additional, countryName, savedEmail);
            reference.child(date_and_time).setValue(newEvent);

            Toast.makeText(this, add_announcement.getText().toString(), Toast.LENGTH_LONG).show();


            String modifiedEmail = savedEmail.replace(".", ",");
            modifiedEmail = modifiedEmail.replace("#", "_");
            modifiedEmail = modifiedEmail.replace("$", "-");
            modifiedEmail = modifiedEmail.replace("[", "(");
            modifiedEmail = modifiedEmail.replace("]", ")");



            reference = database.getReference("CompanyEmails/" + modifiedEmail);

            CompanyEvent companyEvent = new CompanyEvent(date_and_time, calendar.getTime(), countryName);
            reference.child("CompanySocialEvent").setValue(companyEvent);
        }







        Intent intent = new Intent(EventLocalizationPreview.this, MapActivityMain.class);
        intent.putExtra("activity", "main");
        startActivity(intent);

    }
}