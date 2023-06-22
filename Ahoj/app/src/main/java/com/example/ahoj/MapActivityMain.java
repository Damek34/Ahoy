package com.example.ahoj;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Map;

import DatabaseFiles.Setings.SettingsDatabase;


public class MapActivityMain extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Marker marker;
    Marker[] near_events;
    Marker[] markersTab;





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
    List<String> eventLocalizationAll = new ArrayList<>();
    List<String> eventCompanyNameV = new ArrayList<>();
    List<String> eventDateAndTimeV = new ArrayList<>();
    List<String> eventAdditionalV = new ArrayList<>();
    List<Date> eventDateV = new ArrayList<>();


    List<String> announcement_company_nameList = new ArrayList<>();
    List<String> date_and_timeList = new ArrayList<>();
    List<String> date_and_timeList_remove = new ArrayList<>();


    Date date;
    Calendar calendar = Calendar.getInstance();


    boolean can_be_deleted_scan = true;
    boolean show_close_search_btn = false;

    AdView adview;

    TextView your_localization;

    String countryName = "", search_announcements_str = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_main);
        your_localization = findViewById(R.id.TextviewYourLocalizationTranslate);

        date = new Date();

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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
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



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });


        adview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);



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
            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja)).title(String.valueOf(your_localization.getText())));
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
                    .title(String.valueOf(your_localization.getText()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.twojalokalizacja));
            marker = mMap.addMarker(options);
        }

        assert marker != null;
        current_lat = marker.getPosition().latitude;
        current_lng = marker.getPosition().longitude;

        markerOnClick();


    }

    public void scanEvents() throws IOException {

        if(!can_be_deleted_scan){
            return;
        }


        ScanEventsTask task = new ScanEventsTask();
        task.execute();
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
                        calendar.setTime(eventDateV.get(markerIndex));

                        int month = calendar.get(Calendar.MONTH) + 1;

                        eventActivity.putExtra("Name", eventNameV.get(markerIndex));
                        eventActivity.putExtra("Description", eventDescV.get(markerIndex));
                        eventActivity.putExtra("Localization", eventLocalizationV.get(markerIndex));
                        eventActivity.putExtra("Company", eventCompanyNameV.get(markerIndex));
                        eventActivity.putExtra("Duration", eventDateV.get(markerIndex).getHours() + ":" + eventDateV.get(markerIndex).getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                        eventActivity.putExtra("Additional", eventAdditionalV.get(markerIndex));
                        startActivity(eventActivity);
                    } else {
                        Toast.makeText(getApplicationContext(), "Jesteś za daleko", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    void createAndPlaceMarkers() throws IOException {

        near_events_number = 0;


        //create marker
        Marker[] markersTab = new Marker[global_count];
        event_lat = new double[markersTab.length];
        event_lng = new double[markersTab.length];

        double distance;

        List<Address> addressList;
        Address address;


        //place marker
        for (int i = 0; i < near_events.length; i++) {
            if (date.before(eventDateV.get(i))) {
                try {
                    addressList = geocoder.getFromLocationName(eventLocalizationV.get(i), 1);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                int finalI = i;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    near_events[finalI] = mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(eventNameV.get(finalI))).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ahoylocalpin)));
                           // .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinezkalokalna)));
                    near_events[finalI].setTag(finalI);


                    }
                });
            }
        }
        can_be_deleted_scan = true;

        markerOnClick();
    }

    public void openSearch(View view){

        SearchView search = findViewById(R.id.searchLocalization);
        search.onActionViewExpanded();

        Button close = findViewById(R.id.close);

        if(!show_close_search_btn){
            close.setVisibility(View.VISIBLE);
        }


    }

    public void menu(View view){
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setVisibility(View.VISIBLE);
    }

    public void exitMenu(View view){
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setVisibility(View.GONE);
    }

    public void settings(View view){
        Intent intent = new Intent(MapActivityMain.this, SettingActivity.class);
        intent.putExtra("activity", "main");
        startActivity(intent);

    }

    public void add(View view){
        startActivity(new Intent(MapActivityMain.this, EventLocalVirtualAnnouncement.class));
    }

    public void close(View view){
        SearchView search = findViewById(R.id.searchLocalization);
        search.onActionViewCollapsed();

        Button close = findViewById(R.id.close);
        close.setVisibility(View.GONE);
    }



    Button[] buttons;

    public void announcements(View view){
        EditText editTextSearchAnnouncements = findViewById(R.id.editTextSearchAnnouncements);
        Button exit_menu, settings, announcements, exit_announcements, ahoy_announcements, search_announcements;

        LinearLayout linearLayout = findViewById(R.id.menuLinearLayout);

        exit_menu = findViewById(R.id.buttonExitMenu);
        settings = findViewById(R.id.settingsButton);
        announcements = findViewById(R.id.announcementsButton);
        exit_announcements = findViewById(R.id.buttonExitAnnouncements);
        ahoy_announcements = findViewById(R.id.AhoyAnnouncements);
        search_announcements = findViewById(R.id.search_announcements);

        exit_menu.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        announcements.setVisibility(View.GONE);

        exit_announcements.setVisibility(View.VISIBLE);
        ahoy_announcements.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        editTextSearchAnnouncements.setVisibility(View.VISIBLE);
        search_announcements.setVisibility(View.VISIBLE);



        final int[] count = {0};
        final int[] countRemove = {0};



        reference = database.getReference("Announcement/" + countryName);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Date announcement_date_temp =  snapshot.child("time_and_date").getValue(Date.class);
                    Date announcement_date_temp =  snapshot.child("announcement_duration").getValue(Date.class);




                    if(date.before(announcement_date_temp)){
                        count[0]++;
                        announcement_company_nameList.add(snapshot.child("announcement_company_name").getValue(String.class));
                        date_and_timeList.add(snapshot.child("time_and_date").getValue(String.class));
                    }
                    else{
                        countRemove[0]++;
                        date_and_timeList_remove.add(snapshot.child("time_and_date").getValue(String.class));
                    }

                }


                buttons = new Button[count[0]];


                for(int i = 0; i < count[0]; i++){
                    buttons[i] = new Button(MapActivityMain.this);
                    buttons[i].setText(announcement_company_nameList.get(i));
                    buttons[i].setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    linearLayout.addView(buttons[i]);


                    int finalI1 = i;
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(MapActivityMain.this, AnnouncementActivity.class);
                            intent.putExtra("activity", "main");
                            intent.putExtra("id", date_and_timeList.get(finalI1));
                            intent.putExtra("company", announcement_company_nameList.get(finalI1));
                            intent.putExtra("country", countryName);

                            startActivity(intent);
                        }
                    });
                }




                for(int i = 0; i < countRemove[0]; i++){
                    reference = FirebaseDatabase.getInstance().getReference("Announcement/" + countryName).child(date_and_timeList_remove.get(i));
                    reference.removeValue();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void searchAnnouncements(View view){
        EditText search_announcements = findViewById(R.id.editTextSearchAnnouncements);

        search_announcements_str = search_announcements.getText().toString();

        LinearLayout linearLayout = findViewById(R.id.menuLinearLayout);


        if(!search_announcements_str.isEmpty()){
            for(int i = 0; i < buttons.length; i++){
                linearLayout.removeView(buttons[i]);
            }

            for(int i = 0; i < buttons.length; i++){
                if(announcement_company_nameList.get(i).equals(search_announcements_str)){
                    linearLayout.addView(buttons[i]);
                }
            }
        }



    }
    public void exitAnnouncements(View view){
        search_announcements_str = "";

        EditText editTextSearchAnnouncements = findViewById(R.id.editTextSearchAnnouncements);

        Button exit_menu, settings, announcements, exit_announcements, ahoy_announcements, search_announcements;
        LinearLayout linearLayout = findViewById(R.id.menuLinearLayout);

        exit_menu = findViewById(R.id.buttonExitMenu);
        settings = findViewById(R.id.settingsButton);
        announcements = findViewById(R.id.announcementsButton);
        exit_announcements = findViewById(R.id.buttonExitAnnouncements);
        ahoy_announcements = findViewById(R.id.AhoyAnnouncements);
        search_announcements = findViewById(R.id.search_announcements);

        exit_menu.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
        announcements.setVisibility(View.VISIBLE);

        exit_announcements.setVisibility(View.GONE);
        ahoy_announcements.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        editTextSearchAnnouncements.setVisibility(View.GONE);
        search_announcements.setVisibility(View.GONE);




        for(int i = 0; i < buttons.length; i++){
            linearLayout.removeView(buttons[i]);
        }

    }

    public void ahoyAnnouncements(View view){
        Intent intent = new Intent(MapActivityMain.this, AhoyAnnouncements.class);
        intent.putExtra("activity", "main");

        startActivity(intent);
    }

    public void scan(View view) throws IOException {

        if(!can_be_deleted_scan){
            return;
        }
        //remove old markers
        if(global_count != 0){
            if(near_events.length != 0) {
                for (int i = 0; i < near_events.length; i++) {
                    if(near_events[i] != null){
                        near_events[i].remove();
                    }
                }
            }
        }

        scanEvents();
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


    @SuppressLint("StaticFieldLeak")
    private class CreateMarkersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                createAndPlaceMarkers();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            markerOnClick();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class ScanEventsTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            eventNameV.clear();
            eventDescV.clear();
            eventLocalizationV.clear();
            eventLocalizationAll.clear();
            eventCompanyNameV.clear();
            eventDateV.clear();
            eventDateAndTimeV.clear();
            eventAdditionalV.clear();


            date = new Date();

            calendar.setTime(date);



            countryName = "";
            geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(current_lat, current_lng, 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (addresses.size() > 0) {
                countryName = addresses.get(0).getCountryName();
            }


            reference = database.getReference("Event/" + countryName);
            String finalCountryName = countryName;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                int count = 0;

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int i = 0;
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            String eventLocalization = eventSnapshot.child("event_localization").getValue(String.class);
                            eventLocalizationAll.add(eventLocalization);


                            List<Address> addressList = null;
                            Address address = null;

                            try {
                                addressList = geocoder.getFromLocationName(eventLocalizationAll.get(i), 1);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            address = addressList.get(0);
                            double distance = Math.round(calculateDistance(current_lat, current_lng, address.getLatitude(), address.getLongitude()));
                            if(distance <= 20){
                                String eventName = eventSnapshot.child("event_name").getValue(String.class);
                                eventNameV.add(eventName);

                                String eventDescription = eventSnapshot.child("event_description").getValue(String.class);
                                eventDescV.add(eventDescription);

                                eventLocalizationV.add(eventLocalizationAll.get(i));

                                String eventCompanyName = eventSnapshot.child("event_company_name").getValue(String.class);
                                eventCompanyNameV.add(eventCompanyName);

                                Date eventDuration = eventSnapshot.child("event_duration").getValue(Date.class);
                                eventDateV.add(eventDuration);

                                String eventDateTime = eventSnapshot.child("time_and_date").getValue(String.class);
                                eventDateAndTimeV.add(eventDateTime);

                                String eventAdditional = eventSnapshot.child("event_additional").getValue(String.class);
                                eventAdditionalV.add(eventAdditional);
                                near_events_number++;

                            }

                            i++;
                        }

                        geocoder = new Geocoder(MapActivityMain.this);
                        List<Address> addressList = null;
                        Address address = null;

                        localizations = new ArrayList<>(eventLocalizationV);

                        for (int j = 0; j < localizations.size(); j++) {
                            if (date.before(eventDateV.get(j))) {
                                count++;
                            } else {
                                String ref = eventDateAndTimeV.get(j);

                                reference = FirebaseDatabase.getInstance().getReference("Event/" + finalCountryName).child(ref);
                                reference.removeValue();

                            }
                        }




                        //create marker
                        markersTab = new Marker[count];
                        event_lat = new double[markersTab.length];
                        event_lng = new double[markersTab.length];

                        global_count = count;




                        near_events = new Marker[near_events_number];

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

            return null;
        }

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
