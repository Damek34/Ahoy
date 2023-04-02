package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import DatabaseFiles.CountryAge.CountryAgeDatabase;
import DatabaseFiles.Setings.SettingsDatabase;


public class MapActivityMain extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Marker marker;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);

        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();

        if(!connected){
            startActivity(new Intent(MapActivityMain.this, EnableInternetConnection.class));
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);


        CountryAgeDatabase dbAgeCountry = Room.databaseBuilder(getApplicationContext(),
                CountryAgeDatabase.class, "user-database").allowMainThreadQueries().build();

        SettingsDatabase dbSettings = Room.databaseBuilder(getApplicationContext(),
                SettingsDatabase.class, "user-settings-database").allowMainThreadQueries().build();




        SearchView search = (SearchView) findViewById(R.id.searchLocalization);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = search.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapActivityMain.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                   // mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        CountryAgeDatabase dbAgeCountry = Room.databaseBuilder(getApplicationContext(),
                CountryAgeDatabase.class, "user-database").allowMainThreadQueries().build();

        SettingsDatabase dbSettings = Room.databaseBuilder(getApplicationContext(),
                SettingsDatabase.class, "user-settings-database").allowMainThreadQueries().build();
         mMap = googleMap;

         mMap.clear();





        if (dbSettings.settingsDAO().getMapType() == 1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        if (dbSettings.settingsDAO().getMapType() == 2) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        if (dbSettings.settingsDAO().getMapType() == 3) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        if (dbSettings.settingsDAO().getMapType() == 4) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        loadEvents();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //Criteria criteria = new Criteria();
       // String bestProvider = locationManager.getBestProvider(criteria, true);


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        Location location = null;
        try{
             location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch (java.lang.IllegalArgumentException e){
            startActivity(new Intent(MapActivityMain.this, EnableLocalization.class));
        }




        if (location == null) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

        if (location != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja)).title("Twoja lokalizacja"));



        }


    }

   /* można usunąć chyba ale upewnic sie private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
*/


    public void onLocationChanged(Location location) {

        if (marker != null) {
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        } else {
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("My Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja));
            marker = mMap.addMarker(options);
        }

    }

    public void loadEvents(){

        List<String> eventNameV = new ArrayList<>();
        List<String> eventDescV = new ArrayList<>();
        List<String> eventLocalizationV = new ArrayList<>();
        List<String> eventCompanyNameV = new ArrayList<>();
        List<String> eventDateAndTime = new ArrayList<>();
        List<Date> eventDate = new ArrayList<>();



        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        reference = database.getReference("Event");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0, countAfter = 0;

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {

                        String eventName = eventSnapshot.child("event_name").getValue(String.class);
                        eventNameV.add(eventName.toString());

                        String eventDescription = eventSnapshot.child("event_description").getValue(String.class);
                        eventDescV.add(eventDescription.toString());

                        String eventLocalization = eventSnapshot.child("event_localization").getValue(String.class);
                        eventLocalizationV.add(eventLocalization.toString());

                        String eventCompanyName = eventSnapshot.child("event_company_name").getValue(String.class);
                        eventCompanyNameV.add(eventName.toString());

                        Date eventDuration = eventSnapshot.child("event_duration").getValue(Date.class);
                        eventDate.add(eventDuration);

                        String eventDateTime = eventSnapshot.child("time_and_date").getValue(String.class);
                        eventDateAndTime.add(eventDateTime);


                    }

                    Geocoder geocoder = new Geocoder(MapActivityMain.this);
                    List<String> localizations = new ArrayList<>();
                    List<Address> addressList = null;
                    Address address;

                    localizations.addAll(eventLocalizationV);



                    for(int i = 0; i < localizations.size(); i++){
                       if (date.before(eventDate.get(i))) {
                            count++;
                        }
                       else{
                           countAfter++;
                           String ref = eventDateAndTime.get(i).toString();

                           reference = database.getInstance().getReference("Event").child(ref);
                           reference.removeValue();

                       }
                    }

                    Marker[] markersTab = new Marker[count];
                    int[] afterTab = new int[countAfter];


                    for (int i = 0; i < markersTab.length; i++) {
                        if (date.before(eventDate.get(i))){
                            try {
                                addressList = geocoder.getFromLocationName(localizations.get(i), 1);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                            markersTab[i] = mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(eventNameV.get(i))));

                        }
                        else {
                            markersTab[i].remove();
                        }
                    }


                    for(int i = 0; i < markersTab.length; i++){
                        Marker marker_Open = markersTab[i];
                        int finalI = i;
                        marker_Open.setTag(finalI);

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(@NonNull Marker markerr) {

                                if(markerr.getTag() == marker.getTag()){
                                    return;
                                }

                                Intent eventActivity = new Intent(MapActivityMain.this, EventActivity.class);

                                int markerIndex = (int) markerr.getTag();
                                eventActivity.putExtra("Name", eventNameV.get(markerIndex));

                                startActivity(eventActivity);

                            }
                        });

                    }


        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void openSearch(View view){

        SearchView searchView = (SearchView) findViewById(R.id.searchLocalization);
         searchView.onActionViewExpanded();
    }

    public void settings(View view){

        startActivity(new Intent(MapActivityMain.this, SettingActivity.class));

    }

    public void add(View view){
        startActivity(new Intent(MapActivityMain.this, EventLocalOrVirtual.class));
    }


}