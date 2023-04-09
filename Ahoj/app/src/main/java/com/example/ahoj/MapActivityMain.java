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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import DatabaseFiles.Setings.SettingsDatabase;




public class MapActivityMain extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Marker marker;
    Marker[] near_events;


    List<String> near_localizations = new ArrayList<>();
    List<Date> near_event_date = new ArrayList<>();
    List<String> near_event_name = new ArrayList<>();
    List<String> near_event_desc = new ArrayList<>();
    List<String> near_event_company_name = new ArrayList<>();
    List<String> near_event_additional = new ArrayList<>();


    int global_count = 0;
    List<String> localizations;
    Geocoder geocoder;
    int near_events_number = 0;


    double current_lat;
    double current_lng;

    double[] event_lat;
    double[] event_lng;


    List<String> eventNameV = new ArrayList<>();
    List<String> eventDescV = new ArrayList<>();
    List<String> eventLocalizationV = new ArrayList<>();
    List<String> eventCompanyNameV = new ArrayList<>();
    List<String> eventDateAndTimeV = new ArrayList<>();
    List<String> eventAdditionalV = new ArrayList<>();
    List<Date> eventDate = new ArrayList<>();



    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    Marker[] markersTab;





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

        SearchView search = findViewById(R.id.searchLocalization);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = search.getQuery().toString();
                List<Address> addressList = null;

                Geocoder geocoder = new Geocoder(MapActivityMain.this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert addressList != null;

                Address address = null;
                try{
                    address = addressList.get(0);
                }
                catch (IndexOutOfBoundsException ignored){}

                LatLng latLng = null;

                try{
                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                }
                catch (NullPointerException ignored){}

                // mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(), "Nie odnaleziono takiego miejsca", Toast.LENGTH_LONG).show();
                }


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        // CountryAgeDatabase dbAgeCountry = Room.databaseBuilder(getApplicationContext(),
        //   CountryAgeDatabase.class, "user-database").allowMainThreadQueries().build();

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
            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja)).title("Twoja lokalizacja"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15), 1500, null);

            assert marker != null;
            current_lat = marker.getPosition().latitude;
            current_lng = marker.getPosition().longitude;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        scanEvents();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 1800);





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

        assert marker != null;
        current_lat = marker.getPosition().latitude;
        current_lng = marker.getPosition().longitude;

        markerOnClick();


    }

    public void scanEvents() throws IOException {

        eventNameV.clear();
        eventDescV.clear();
        eventLocalizationV.clear();
        eventCompanyNameV.clear();
        eventDate.clear();
        eventDateAndTimeV.clear();
        eventAdditionalV.clear();

        near_events_number = 0;
        near_localizations.clear();
        near_event_date.clear();
        near_event_name.clear();
        near_event_desc.clear();
        near_event_company_name.clear();
        near_event_additional.clear();


        calendar.setTime(date);
        String countryName = "";
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(current_lat, current_lng, 1);

        if (addresses.size() > 0) {
            countryName = addresses.get(0).getCountryName();
        }


        reference = database.getReference("Event/" + countryName);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            int count = 0;

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        String eventName = eventSnapshot.child("event_name").getValue(String.class);
                        eventNameV.add(eventName);

                        String eventDescription = eventSnapshot.child("event_description").getValue(String.class);
                        eventDescV.add(eventDescription);

                        String eventLocalization = eventSnapshot.child("event_localization").getValue(String.class);
                        eventLocalizationV.add(eventLocalization);

                        String eventCompanyName = eventSnapshot.child("event_company_name").getValue(String.class);
                        eventCompanyNameV.add(eventCompanyName);

                        Date eventDuration = eventSnapshot.child("event_duration").getValue(Date.class);
                        eventDate.add(eventDuration);

                        String eventDateTime = eventSnapshot.child("time_and_date").getValue(String.class);
                        eventDateAndTimeV.add(eventDateTime);

                        String eventAdditional = eventSnapshot.child("event_additional").getValue(String.class);
                        eventAdditionalV.add(eventAdditional);

                    }

                    geocoder = new Geocoder(MapActivityMain.this);
                    List<Address> addressList = null;
                    Address address = null;

                    localizations = new ArrayList<>(eventLocalizationV);


                    for (int i = 0; i < localizations.size(); i++) {
                        if (date.before(eventDate.get(i))) {
                            count++;
                        } else {
                            String ref = eventDateAndTimeV.get(i);

                            reference = FirebaseDatabase.getInstance().getReference("Event").child(ref);
                            reference.removeValue();

                        }
                    }


                    //create marker
                    markersTab = new Marker[count];
                    event_lat = new double[markersTab.length];
                    event_lng = new double[markersTab.length];

                    global_count = count;

                    double distance;


                    for (int i = 0; i < markersTab.length; i++) {
                        if (date.before(eventDate.get(i))) {
                            try {
                                addressList = geocoder.getFromLocationName(localizations.get(i), 1);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            address = addressList.get(0);

                            event_lat[i] = address.getLatitude();
                            event_lng[i] = address.getLongitude();

                            distance = Math.round(calculateDistance(current_lat, current_lng, event_lat[i], event_lng[i]));

                            if (distance <= 20) {
                                near_events_number++;
                                near_localizations.add(localizations.get(i));
                                near_event_date.add(eventDate.get(i));
                                near_event_name.add(eventNameV.get(i));
                                near_event_desc.add(eventDescV.get(i));
                                near_event_company_name.add(eventCompanyNameV.get(i));
                                near_event_additional.add(eventAdditionalV.get(i));
                            }
                        }
                    }



                    near_events = new Marker[near_events_number];

                    //create and place markers
                    /*
                    try {
                        createAndPlaceMarkers();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                     */
                    CreateMarkersTask task = new CreateMarkersTask();
                    task.execute();
                }
                else{
                    global_count = 0;
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
    double calculateDistance(double lat1, double lng1, double lat2, double lng2){
        lng1 = Math.toRadians(lng1);
        lng2 = Math.toRadians(lng2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dlon = lng2 - lng1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return(c * 6371);
    }

    void markerOnClick(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.setOnInfoWindowClickListener(markerr -> {

                    if(markerr.getTag() == marker.getTag()){
                        return;
                    }

                    double distance = calculateDistance(current_lat, current_lng, markerr.getPosition().latitude, markerr.getPosition().longitude);
                    if (distance <= 0.2) {
                        int markerIndex = (int) markerr.getTag();
                        Intent eventActivity = new Intent(MapActivityMain.this, EventActivity.class);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(near_event_date.get(markerIndex));

                        int month = calendar.get(Calendar.MONTH) + 1;

                        eventActivity.putExtra("Name", near_event_name.get(markerIndex));
                        eventActivity.putExtra("Description", near_event_desc.get(markerIndex));
                        eventActivity.putExtra("Localization", near_localizations.get(markerIndex));
                        eventActivity.putExtra("Company", near_event_company_name.get(markerIndex));
                        eventActivity.putExtra("Duration", near_event_date.get(markerIndex).getHours() + ":" + near_event_date.get(markerIndex).getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                        eventActivity.putExtra("Additional", near_event_additional.get(markerIndex));
                        startActivity(eventActivity);
                    } else {
                        Toast.makeText(getApplicationContext(), "Jesteś za daleko", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    void createAndPlaceMarkers() throws IOException {

        //create marker
        Marker[] markersTab = new Marker[global_count];
        event_lat = new double[markersTab.length];
        event_lng = new double[markersTab.length];


        double distance;

        List<Address> addressList;
        Address address;

        for (int i = 0; i < markersTab.length; i++) {
            if (date.before(eventDate.get(i))) {
                try {
                    addressList = geocoder.getFromLocationName(localizations.get(i), 1);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                address = addressList.get(0);

                event_lat[i] = address.getLatitude();
                event_lng[i] = address.getLongitude();

                distance = Math.round(calculateDistance(current_lat, current_lng, event_lat[i], event_lng[i]));

                if (distance <= 20) {
                    near_events_number++;
                    near_localizations.add(localizations.get(i));
                    near_event_date.add(eventDate.get(i));
                    near_event_name.add(eventNameV.get(i));
                    near_event_desc.add(eventDescV.get(i));
                    near_event_company_name.add(eventCompanyNameV.get(i));
                    near_event_additional.add(eventAdditionalV.get(i));
                }
            }
        }


        near_events = new Marker[near_events_number];


        //place marker
        for (int i = 0; i < near_events.length; i++) {
            if (date.before(near_event_date.get(i))) {
                try {
                    addressList = geocoder.getFromLocationName(near_localizations.get(i), 1);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    near_events[finalI] = mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(near_event_name.get(finalI))));
                    near_events[finalI].setTag(finalI);
                    }
                });

            } else {
                near_events[i].remove();
            }
        }

        markerOnClick();
    }

    public void openSearch(View view){

        SearchView searchView = findViewById(R.id.searchLocalization);
        searchView.onActionViewExpanded();
    }

    public void settings(View view){

        startActivity(new Intent(MapActivityMain.this, SettingActivity.class));

    }

    public void add(View view){
        startActivity(new Intent(MapActivityMain.this, EventLocalOrVirtual.class));
    }

    public void scan(View view) throws IOException {
        //remove old markers
        if(global_count == 0){
            return;
        }
        for(int i = 0; i < near_events.length; i++){
            near_events[i].remove();
        }
        scanEvents();
    }

    public void refresh(View view) throws IOException {
        //remove old markers

        if(global_count == 0){
            return;
        }

        for(int i = 0; i < near_events.length; i++){
            near_events[i].remove();
        }
        

        CreateMarkersTask task = new CreateMarkersTask();
        task.execute();

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    private class CreateMarkersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Call the createAndPlaceMarkers() method here
            try {
                createAndPlaceMarkers();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Call any UI-related code here that needs to be executed after the task is finished
            markerOnClick();
        }
    }
}
