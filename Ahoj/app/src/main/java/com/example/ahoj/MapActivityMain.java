package com.example.ahoj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

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
           // location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Twoja Lokalizacja").icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja)));



        }


    }

   /* można usunąć chyba ale upewnic sie private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
*/


    public void onLocationChanged(Location location) {
        //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Twoja lokalizacja");
        // Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Aktualna pozycja po zmianie położenia"));
        // marker.remove();
        // mMap.clear();

        // Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Twoja Lokalizacja").icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja)));
        //  marker.remove();

        //marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Twoja Lokalizacja").icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja)));


        // mMap.addMarker(markerOptions);


        // marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));


        if (marker != null) {
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        } else {
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("My Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja));
            marker = mMap.addMarker(options);

            //loadEvents();
        }

    /*public void updateMarker(Marker marker){
        marker.remove();
    }
     */
    }
    public void settings(View view){

        startActivity(new Intent(MapActivityMain.this, SettingActivity.class));

    }

    public void add(View view){
        startActivity(new Intent(MapActivityMain.this, AddEvent.class));
    }

    public void loadEvents(){

        List<String> eventNameV = new ArrayList<>();
        List<String> eventDescV = new ArrayList<>();
        List<String> eventLocalizationV = new ArrayList<>();
        List<String> eventCompanyNameV = new ArrayList<>();


        reference = database.getReference("Event");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    }
                    Geocoder geocoder = new Geocoder(MapActivityMain.this);
                    List<String> localizations = new ArrayList<>();
                    List<Address> addressList = null;
                    Address address;

                    localizations.addAll(eventLocalizationV);

                   // Toast.makeText(getApplicationContext(), String.valueOf(localizations.get(0)), Toast.LENGTH_LONG).show();


                   /* for(int i = 0; i < localizations.size()-1; i++){

                        try {
                            addressList = geocoder.getFromLocationName(localizations.get(i), 1);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        address = addressList.get(i);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(eventNameV)));


                    }*/

                    int i = 0;
                    for(String loc : localizations){
                        try {
                            addressList = geocoder.getFromLocationName(loc, 1);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(eventNameV.get(i))));
                        i++;
                    }


               //     Toast.makeText(getApplicationContext(), localizations.get(0) + localizations.get(1) + " test", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });






        //geocoder.getFromLocationName(eventLocalizationV., 1);

    }

    public void openSearch(View view){
        SearchView searchView = (SearchView) findViewById(R.id.searchLocalization);
         searchView.onActionViewExpanded();
    }


}