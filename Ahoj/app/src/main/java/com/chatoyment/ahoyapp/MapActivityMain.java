package com.chatoyment.ahoyapp;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chatoyment.ahoyapp.OnlyJava.EncryptionHelper;
import com.chatoyment.ahoyapp.OnlyJava.OnlineDate;
import com.chatoyment.ahoyapp.R;
import com.chatoyment.ahoyapp.Setup.setup;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.concurrent.TimeoutException;


public class MapActivityMain extends AppCompatActivity implements OnMapReadyCallback, LocationListener, OnlineDate.OnDateFetchedListener {

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
    List<String> eventLocalizationV = new ArrayList<>();
    List<String> eventLocalizationAll = new ArrayList<>();
    List<String> eventDateAndTimeV = new ArrayList<>();
    List<String> eventOrganizerV = new ArrayList<>();
    List<Date> eventDateV = new ArrayList<>();
    List<Boolean> eventRestriction = new ArrayList<>();




    List<String> announcement_company_nameList = new ArrayList<>();
    List<String> date_and_timeList = new ArrayList<>();
    List<String> date_and_timeList_remove = new ArrayList<>();
    List<String> announcement_organizer_List_remove = new ArrayList<>();
    List<Boolean> announcement_restriction_list = new ArrayList<>();


    public Date date;
    Calendar calendar = Calendar.getInstance();


    boolean can_be_deleted_scan = true;
    boolean show_close_search_btn = false;
    boolean social_mode = false;
    boolean can_scan_events = true;
    boolean is_announcements_page_on = false;
    boolean can_say_you_can_collect_extra_point = true;
    boolean display_adult_content;

    AdView adview;

    TextView your_localization, check_internet_connection, failed_location, promotional_mode, social_modeTextView, no_such_place_has_been_found, you_can_collect_extra_point,
            something_went_wrong, no_events_found, downloadingdata;

    String countryName = "", search_announcements_str = "", nick = "";

    Button add_button, points_button, manage_button, my_profile_button, competitions_button;

    Intent activity_intent;

    NavigationView navigationView;

    EditText search_announcements;

    Switch socialSwitch;

    Toolbar bottom_menu;

    ConstraintLayout searchLayout;

    private SharedPreferences sharedPreferences, sharedPreferencesUser, sharedPreferencesNick;

    int scan_radius, zoom_size;

    private Location previousLocation;

    AppCompatButton scan_button;

    boolean scan_button_animation = false, downloading_data_animation = false;

    @SuppressLint("MissingInflatedId")
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
        setContentView(R.layout.activity_map_main);


        your_localization = findViewById(R.id.TextviewYourLocalizationTranslate);
        check_internet_connection = findViewById(R.id.check_internet_connection_map);
        failed_location = findViewById(R.id.failed_location);

        points_button = findViewById(R.id.points_button);
        search_announcements = findViewById(R.id.editTextSearchAnnouncements);
        manage_button = findViewById(R.id.manage_button);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        social_mode = sharedPreferences.getBoolean("social_mode_key", false);
        socialSwitch = findViewById(R.id.socialSwitch);
        promotional_mode = findViewById(R.id.promotional_mode);
        social_modeTextView = findViewById(R.id.social_mode);
        bottom_menu = findViewById(R.id.bottom_menu);
        searchLayout = findViewById(R.id.searchLayout);
        no_such_place_has_been_found = findViewById(R.id.no_such_place_has_been_found);
        my_profile_button = findViewById(R.id.my_profile_button);
        you_can_collect_extra_point = findViewById(R.id.you_can_collect_extra_point);
        something_went_wrong = findViewById(R.id.something_went_wrong);
        competitions_button = findViewById(R.id.competitions_button);
        no_events_found = findViewById(R.id.no_events_found);
        scan_button = findViewById(R.id.scan_button);
        downloadingdata = findViewById(R.id.downloadingdata);
        if(social_mode){
            socialSwitch.setChecked(true);
        }

      //  date = OnlineDate.getDate();

        add_button = findViewById(R.id.addButton);
        activity_intent = getIntent();

        if (activity_intent.getStringExtra("activity").equals("user")) {
            add_button.setVisibility(View.GONE);
        } else {
            points_button.setVisibility(View.GONE);
            my_profile_button.setVisibility(View.GONE);
            manage_button.setVisibility(View.VISIBLE);
        }


        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            if (activity_intent.getStringExtra("activity").equals("user")) {
                Intent intent = new Intent(MapActivityMain.this, EnableInternetConnection.class);
                intent.putExtra("from_activity", "user");
                startActivity(intent);
            } else {
                Intent intent = new Intent(MapActivityMain.this, EnableInternetConnection.class);
                intent.putExtra("from_activity", "main");
                startActivity(intent);
            }

        }

        if (!checkLocationPermission()) {
            Intent intent = new Intent(MapActivityMain.this, EnableLocalization.class);
            intent.putExtra("activity", activity_intent.getStringExtra("activity"));
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }


        sharedPreferencesNick = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        nick = sharedPreferencesNick.getString("nick", "");

        sharedPreferencesUser = getSharedPreferences(nick, Context.MODE_PRIVATE);

        scan_radius = sharedPreferencesUser.getInt("scanning_radius", 20);
        zoom_size = sharedPreferencesUser.getInt("zoom_size", 15);
        display_adult_content = sharedPreferencesUser.getBoolean("display_adult_content", true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

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
                try {
                    address = addressList.get(0);
                } catch (IndexOutOfBoundsException ignored) {
                }

                LatLng latLng = null;

                try {
                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                } catch (NullPointerException ignored) {
                }

                // mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom_size), 1500, null);
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), no_such_place_has_been_found.getText().toString(), Toast.LENGTH_LONG).show();
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
        mMap = googleMap;

        mMap.clear();

        Handler switchHandler = new Handler();
        Runnable switchRunnable[] = {null};


        socialSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchRunnable[0] != null) {
                    switchHandler.removeCallbacks(switchRunnable[0]);
                }

                final boolean[] switchState = {false};
                switchRunnable[0] = new Runnable() {
                    @Override
                    public void run() {
                        switchState[0] = isChecked;

                        if (isChecked) {
                            Toast.makeText(getApplicationContext(), social_modeTextView.getText().toString(), Toast.LENGTH_SHORT).show();


                            social_mode = true;
                            mMap.clear();
                            showMyLocation();

                            if(is_announcements_page_on){
                                exitAnnouncements(null);
                                announcements(null);
                            }


                            announcement_company_nameList.clear();
                            date_and_timeList.clear();
                            date_and_timeList_remove.clear();
                            announcement_organizer_List_remove.clear();

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("social_mode_key", social_mode);
                            editor.apply();

                            if(can_scan_events){
                                eventNameV.clear();
                                eventLocalizationV.clear();
                                eventLocalizationAll.clear();
                                eventDateAndTimeV.clear();
                                eventOrganizerV.clear();
                                eventDateV.clear();
                                eventRestriction.clear();

                                can_scan_events = false;
                                try {
                                    scanEvents();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), promotional_mode.getText().toString(), Toast.LENGTH_SHORT).show();

                            social_mode = false;
                            mMap.clear();
                            showMyLocation();

                            if(is_announcements_page_on){
                                exitAnnouncements(null);
                                announcements(null);
                            }

                            announcement_company_nameList.clear();
                            date_and_timeList.clear();
                            date_and_timeList_remove.clear();
                            announcement_organizer_List_remove.clear();
                            announcement_restriction_list.clear();

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("social_mode_key", social_mode);
                            editor.apply();
                            if(can_scan_events){
                                eventNameV.clear();
                                eventLocalizationV.clear();
                                eventLocalizationAll.clear();
                                eventDateAndTimeV.clear();
                                eventOrganizerV.clear();
                                eventDateV.clear();
                                eventRestriction.clear();

                                can_scan_events = false;
                                try {
                                    scanEvents();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }
                    }
                };
                switchHandler.postDelayed(switchRunnable[0], 750);
            }
        });

        String mapType = sharedPreferencesUser.getString("map_type", "normal");
        if (mapType.equals("normal")) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        if (mapType.equals("terrain")) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        if (mapType.equals("satellite")) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        if (mapType.equals("hybrid")) {
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


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        Location location = null;

        try {
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                // Lokalizacja jest wyłączona
                Intent intent = new Intent(MapActivityMain.this, EnableLocalization.class);
                intent.putExtra("activity", activity_intent.getStringExtra("activity"));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                // Lokalizacja jest włączona, możesz kontynuować pobieranie lokalizacji
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null) {
                    // Tutaj możesz podjąć dodatkowe kroki w przypadku braku dostępnej lokalizacji
                }
            }

        } catch (SecurityException e) {
            // Obsługa błędu związanego z uprawnieniami
        }
        if (location != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.localization_tracker)).title(String.valueOf(your_localization.getText())));
            previousLocation = location;

          /*  SharedPreferences.Editor userEditor = sharedPreferencesUser.edit();
            userEditor.putString("previous_location", String.valueOf(location));
            userEditor.apply();

            sharedPreferencesUser.getString("previous_location", String.valueOf(location));

           */

            boolean is_auto_zoom_on = sharedPreferencesUser.getBoolean("auto_zoom", true);
            if(is_auto_zoom_on){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom_size), 1500, null);
            }

            assert marker != null;
            current_lat = marker.getPosition().latitude;
            current_lng = marker.getPosition().longitude;

            /*
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


             */
            fetchDateAndScanEvents();

        }


    }

    public void onLocationChanged(Location location) {

        if (marker != null) {
            marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        } else {
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(String.valueOf(your_localization.getText()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.localization_tracker));
            marker = mMap.addMarker(options);
        }

        if(activity_intent.getStringExtra("activity").equals("user")){
            //przetestować
            if (previousLocation != null && location != null){
                float distance = previousLocation.distanceTo(location);


                if (distance < 10) {
                    float total_distance = sharedPreferencesUser.getFloat("total_distance", 0);

                    SharedPreferences.Editor userEditor = sharedPreferencesUser.edit();

                    float final_distance = distance + total_distance;

                    if(final_distance >= 100){
                        userEditor.putFloat("total_distance", 0);
//
                        reference = database.getReference("Nick/" + nick + "/distance");
                        reference.setValue(final_distance);
                    }
                    else{
                        userEditor.putFloat("total_distance", total_distance + distance);
                    }

                    if(sharedPreferencesUser.getFloat("distance", 0) <= 5000){
                        float distance_for_points = sharedPreferencesUser.getFloat("distance", 0);
                        userEditor.putFloat("distance", distance_for_points + distance);

                    }
                    else{
                        if(sharedPreferencesUser.getBoolean("notification_every_5_km", true)){
                            if(can_say_you_can_collect_extra_point){
                                can_say_you_can_collect_extra_point = false;
                                Toast.makeText(getApplicationContext(), you_can_collect_extra_point.getText().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    userEditor.apply();
                }



                previousLocation = location;
            }
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
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();

        if (!connected) {
            return;
        }
        if(date == null){
            Toast.makeText(getApplicationContext(), "data jest nullem", Toast.LENGTH_LONG).show();
            return;
        }
      //  OnlineDate.fetchDateAsync();

       // date = OnlineDate.getDate();
       // OnlineDate onlineDate = new OnlineDate();
       // Toast.makeText(getApplicationContext(), String.valueOf(OnlineDate.getDate()), Toast.LENGTH_LONG).show();

        ScanEventsTask task = new ScanEventsTask();
        task.execute();
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
        float animationDurationFloat = sharedPreferencesUser.getFloat("menu_animation_duration", 0.4f);
        long animationDurationMillis = (long) (animationDurationFloat * 1000);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_to_right_menu);
        Animation animationToBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_to_bottom_bottom_menu);
        Animation animationSearchToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_top_search);

        animation.setDuration(animationDurationMillis);
        animationToBottom.setDuration(animationDurationMillis);
        animationSearchToTop.setDuration(animationDurationMillis);


        navigationView = findViewById(R.id.navigation);



        bottom_menu.startAnimation(animationToBottom);
        searchLayout.startAnimation(animationSearchToTop);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottom_menu.setVisibility(View.GONE);
                searchLayout.setVisibility(View.GONE);
                navigationView.setVisibility(View.VISIBLE);
                navigationView.startAnimation(animation);
            }
        }, animationDurationMillis);

    }

    public void exitMenu(View view){

        float animationDurationFloat = sharedPreferencesUser.getFloat("menu_animation_duration", 0.4f);
        long animationDurationMillis = (long) (animationDurationFloat * 1000);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_menu);
        Animation animationToTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_top_bottom_menu);
        Animation animationSearchToBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_to_bottom_search);

        animation.setDuration(animationDurationMillis);
        animationToTop.setDuration(animationDurationMillis);
        animationSearchToBottom.setDuration(animationDurationMillis);



        NavigationView navigationView = findViewById(R.id.navigation);

        navigationView.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigationView.setVisibility(View.GONE);
                bottom_menu.setVisibility(View.VISIBLE);
                bottom_menu.startAnimation(animationToTop);
                searchLayout.setVisibility(View.VISIBLE);
                searchLayout.startAnimation(animationSearchToBottom);

            }
        }, animationDurationMillis);

        //bottom_menu.startAnimation(animationToTop);

    }

    public void settings(View view){
        Intent intent = new Intent(MapActivityMain.this, SettingActivity.class);
        if(activity_intent.getStringExtra("activity").equals("main")){
            intent.putExtra("activity", "main");
        }
        else{
            intent.putExtra("activity", "user");
        }
        startActivity(intent);

    }

    public void add(View view){
        startActivity(new Intent(MapActivityMain.this, SelectWhatToAdd.class));
    }
    public void close(View view){
        SearchView search = findViewById(R.id.searchLocalization);
        search.onActionViewCollapsed();

        Button close = findViewById(R.id.close);
        close.setVisibility(View.GONE);
    }
    public void pointsActivity(View view){
        startActivity(new Intent(MapActivityMain.this, Points.class));

    }
    Button[] buttons;

    public void announcements(View view){

        if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), check_internet_connection.getText().toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if(countryName.isEmpty()){
            geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(current_lat, current_lng, 1);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), failed_location.getText().toString(), Toast.LENGTH_LONG).show();

            }



            if (addresses != null && addresses.size() > 0 ) {
                countryName = addresses.get(0).getCountryName();
            }
            else{
                Toast.makeText(getApplicationContext(), failed_location.getText().toString(), Toast.LENGTH_LONG).show();
            }
        }

        is_announcements_page_on = true;
        LinearLayout linearLayout = findViewById(R.id.menuLinearLayout);
        TextView thereisnoannouncements = findViewById(R.id.thereisnoannouncements);

        search_announcements.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!search_announcements.getText().toString().trim().equals("")){
                    //thereisnoannouncements.setVisibility(View.GONE);
                    searchButtons(s.toString());
                }
                else{
                    for(int i = 0; i < linearLayout.getChildCount(); i++){
                        linearLayout.getChildAt(i).setVisibility(View.VISIBLE);
                        thereisnoannouncements.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        EditText editTextSearchAnnouncements = findViewById(R.id.editTextSearchAnnouncements);
        Button exit_menu, settings, announcements, exit_announcements, ahoy_announcements, search_announcements, manage;




        exit_menu = findViewById(R.id.buttonExitMenu);
        settings = findViewById(R.id.settingsButton);
        announcements = findViewById(R.id.announcementsButton);
        exit_announcements = findViewById(R.id.buttonExitAnnouncements);
        ahoy_announcements = findViewById(R.id.AhoyAnnouncements);


        manage = findViewById(R.id.manage_button);




        exit_menu.setVisibility(View.GONE);
        settings.setVisibility(View.GONE);
        announcements.setVisibility(View.GONE);


        points_button.setVisibility(View.GONE);
        manage_button.setVisibility(View.GONE);
        my_profile_button.setVisibility(View.GONE);
        competitions_button.setVisibility(View.GONE);


        exit_announcements.setVisibility(View.VISIBLE);
        ahoy_announcements.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        editTextSearchAnnouncements.setVisibility(View.VISIBLE);



        manage.setVisibility(View.GONE);



        final int[] count = {0};
        final int[] countRemove = {0};


        if(social_mode){
            reference = database.getReference("SocialAnnouncement/" + countryName);
        }
        else{
            reference = database.getReference("Announcement/" + countryName);
        }

        downloadingdata.setVisibility(View.VISIBLE);

        downloading_data_animation = true;
        downloadingDataAnimation();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Boolean is_for_adults_only = snapshot.child("age_restricted").getValue(Boolean.class);
                    if(!(is_for_adults_only == true && display_adult_content == false)){
                        Date announcement_date_temp =  snapshot.child("announcement_duration").getValue(Date.class);

                        try {
                            if(date.before(announcement_date_temp)){
                                count[0]++;
                                announcement_company_nameList.add(snapshot.child("announcement_company_name").getValue(String.class));
                                announcement_restriction_list.add(snapshot.child("age_restricted").getValue(Boolean.class));
                                date_and_timeList.add(snapshot.child("time_and_date").getValue(String.class));
                            }
                            else{
                                countRemove[0]++;
                                date_and_timeList_remove.add(snapshot.child("time_and_date").getValue(String.class));
                                announcement_organizer_List_remove.add(snapshot.child("organizer").getValue(String.class));
                            }
                        }
                        catch (NullPointerException e){
                            //   date = OnlineDate.getDate();
                            navigationView.setVisibility(View.GONE);


                            exit_menu.setVisibility(View.VISIBLE);
                            settings.setVisibility(View.VISIBLE);
                            announcements.setVisibility(View.VISIBLE);

                            exit_announcements.setVisibility(View.GONE);
                            ahoy_announcements.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.GONE);
                            editTextSearchAnnouncements.setVisibility(View.GONE);

                            thereisnoannouncements.setVisibility(View.GONE);


                            return;
                        }
                    }




                }

                if(count[0] == 0){
                    thereisnoannouncements.setVisibility(View.VISIBLE);
                    downloading_data_animation = false;
                    downloadingdata.clearAnimation();
                    downloadingdata.setVisibility(View.GONE);
                }


                buttons = new Button[count[0]];


                for(int i = 0; i < count[0]; i++){
                    buttons[i] = new Button(MapActivityMain.this);
                    buttons[i].setText(announcement_company_nameList.get(i));
                    buttons[i].setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    downloading_data_animation = false;
                    downloadingdata.setVisibility(View.GONE);
                    linearLayout.addView(buttons[i]);

                    buttons[i].setBackground(ContextCompat.getDrawable(MapActivityMain.this, R.color.transparent)); // Przykład, gdzie R.drawable.button_background to plik XML z definicją tła przycisku
                    buttons[i].setTextColor(ContextCompat.getColor(MapActivityMain.this, R.color.light_grey)); // Przykład, gdzie R.color.text_color to kolor zdefiniowany w pliku colors.xml
                    int finalI1 = i;
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intent = new Intent(MapActivityMain.this, AnnouncementActivity.class);
                            if(activity_intent.getStringExtra("activity").equals("main")){
                                intent.putExtra("activity", "main");
                                if(social_mode){
                                    intent.putExtra("isSocial", "true");
                                }
                                else{
                                    intent.putExtra("isSocial", "false");
                                }
                            }
                            else{
                                intent.putExtra("activity", "user");
                                if(social_mode){
                                    intent.putExtra("isSocial", "true");
                                }
                                else{
                                    intent.putExtra("isSocial", "false");
                                }
                            }
                            startActivity(intent);
                            intent.putExtra("id", date_and_timeList.get(finalI1));
                            intent.putExtra("company", announcement_company_nameList.get(finalI1));
                            intent.putExtra("country", countryName);
                            intent.putExtra("restricted", announcement_restriction_list.get(0));

                            startActivity(intent);
                        }
                    });
                }
                for (int i = 0; i < countRemove[0]; i++) {
                    String announcementPath;
                    if (social_mode) {
                        announcementPath = "SocialAnnouncement/" + countryName + "/" + date_and_timeList_remove.get(i);
                    } else {
                        announcementPath = "Announcement/" + countryName + "/" + date_and_timeList_remove.get(i);
                    }

                    DatabaseReference announcementRef = FirebaseDatabase.getInstance().getReference(announcementPath);
                    String email = announcement_organizer_List_remove.get(i);


                    // Usuwanie ogłoszenia
                    announcementRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Znajdź i usuń odpowiednie CompanyEmails
                            DatabaseReference companyEmailsRef = FirebaseDatabase.getInstance().getReference("CompanyEmails");
                            companyEmailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                                        String storedEmail = companySnapshot.child("email").getValue(String.class);
                                        if (storedEmail != null && storedEmail.equals(email)) {
                                            String email_date_and_time = companySnapshot.getKey();
                                            Log.e("to_czego_szukam_2", email_date_and_time);

                                            String targetPath;
                                            if (social_mode) {
                                                targetPath = "CompanyEmails/" + email_date_and_time + "/CompanySocialAnnouncement";
                                            } else {
                                                targetPath = "CompanyEmails/" + email_date_and_time + "/CompanyAnnouncement";
                                            }

                                            DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference(targetPath);
                                            targetRef.removeValue();

                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("error_announcement_remove_database", String.valueOf(error));
                                }
                            });
                        } else {
                            Log.e("error_failed_remove_announcement", "fail");
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void searchAnnouncements(View view){
        search_announcements_str = search_announcements.getText().toString();
        search_announcements_str.toLowerCase();

        LinearLayout linearLayout = findViewById(R.id.menuLinearLayout);
        if(!search_announcements_str.trim().isEmpty()){
            for(int i = 0; i < buttons.length; i++){
                linearLayout.removeView(buttons[i]);

            }

            for(int i = 0; i < buttons.length; i++){
                if(announcement_company_nameList.get(i).toLowerCase().contains(search_announcements_str)){
                    linearLayout.addView(buttons[i]);
                }
            }
        }

        if(search_announcements_str.trim().isEmpty()){
            linearLayout.removeAllViews();
            if(buttons != null){
                for(int i = 0; i < buttons.length; i++){
                    linearLayout.addView(buttons[i]);
                }
            }

        }
    }
    void searchButtons(String query) {
        boolean foundMatch = false;
        LinearLayout linearLayout = findViewById(R.id.menuLinearLayout);
        TextView thereisnoannouncements = findViewById(R.id.thereisnoannouncements);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof Button) {
                Button button = (Button) view;
                String title = button.getText().toString();
                if (title.toLowerCase().contains(query.toLowerCase())) {
                    button.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    thereisnoannouncements.setVisibility(View.GONE);
                    foundMatch = true;
                }

                else {
                    button.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }
            }
        }
        if (!foundMatch) {
            thereisnoannouncements.setVisibility(View.VISIBLE);
        }


    }
    public void exitAnnouncements(View view){
        search_announcements.setText("");
        search_announcements_str = "";

        if(activity_intent.getStringExtra("activity").equals("user")){
            points_button.setVisibility(View.VISIBLE);
            manage_button.setVisibility(View.GONE);
            my_profile_button.setVisibility(View.VISIBLE);
        }
        else{
            points_button.setVisibility(View.GONE);
            manage_button.setVisibility(View.VISIBLE);
            my_profile_button.setVisibility(View.GONE);
        }
        is_announcements_page_on = false;

        EditText editTextSearchAnnouncements = findViewById(R.id.editTextSearchAnnouncements);
        TextView thereisnoannouncements = findViewById(R.id.thereisnoannouncements);
        Button exit_menu, settings, announcements, exit_announcements, ahoy_announcements, search_announcements;
        LinearLayout linearLayout = findViewById(R.id.menuLinearLayout);


        exit_menu = findViewById(R.id.buttonExitMenu);
        settings = findViewById(R.id.settingsButton);
        announcements = findViewById(R.id.announcementsButton);
        exit_announcements = findViewById(R.id.buttonExitAnnouncements);
        ahoy_announcements = findViewById(R.id.AhoyAnnouncements);


        exit_menu.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
        announcements.setVisibility(View.VISIBLE);
        competitions_button.setVisibility(View.VISIBLE);

        exit_announcements.setVisibility(View.GONE);
        ahoy_announcements.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        editTextSearchAnnouncements.setVisibility(View.GONE);

        thereisnoannouncements.setVisibility(View.GONE);
        downloadingdata.setVisibility(View.GONE);





        if(buttons == null){
            return;
        }

        for(int i = 0; i < buttons.length; i++){
            linearLayout.removeView(buttons[i]);
        }

    }
    public void ahoyAnnouncements(View view){
        Intent intent = new Intent(MapActivityMain.this, AhoyAnnouncements.class);
        if(activity_intent.getStringExtra("activity").equals("main")){
            intent.putExtra("activity", "main");
        }
        else{
            intent.putExtra("activity", "user");
        }
        startActivity(intent);
    }
    public void manageActivity(View view){
        Intent intent = new Intent(MapActivityMain.this, Manage.class);
        if(activity_intent.getStringExtra("activity").equals("main")){
            intent.putExtra("activity", "main");
        }
        else{
            intent.putExtra("activity", "user");
        }
        startActivity(intent);
    }
    public void scan(View view) throws IOException {

        if(!can_be_deleted_scan){
            return;
        }
        scan_button_animation = true;
        scanButtonAnimation();
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

       // scanEvents();
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    scanEvents();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

         */

        fetchDateAndScanEvents();
    }

    public void profileActivity(View view){
        Intent intent = new Intent(MapActivityMain.this, ProfileActivity.class);
        intent.putExtra("nick", nick);
        intent.putExtra("from_leaderboard", "false");
        startActivity(intent);
    }

    public void competitionsActivity(View view){
        Intent intent = new Intent(MapActivityMain.this, CompetitionsActivity.class);
        if(activity_intent.getStringExtra("activity").equals("main")){
            intent.putExtra("activity", "main");
        }
        else{
            intent.putExtra("activity", "user");
        }
        intent.putExtra("country", countryName);
        startActivity(intent);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onFlushComplete(int requestCode) {
     //   LocationListener.super.onFlushComplete(requestCode);
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
    private class ScanEventsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            eventNameV.clear();
           // eventDescV.clear();
            eventLocalizationV.clear();
            eventLocalizationAll.clear();
            eventOrganizerV.clear();
          //  eventCompanyNameV.clear();
            eventDateV.clear();
            eventRestriction.clear();
         //   eventDateAndTimeV.clear();
        //    eventAdditionalV.clear();


          //  date = OnlineDate.getDate();

            calendar.setTime(date);



            countryName = "";
            geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(current_lat, current_lng, 1);
            } catch (IOException e) {
              //  throw new RuntimeException(e);
            }

            if (addresses.size() > 0) {
                countryName = addresses.get(0).getCountryName();
            }


            if(!social_mode){
                reference = database.getReference("Event/" + countryName);
            }
            else{
                reference = database.getReference("SocialEvent/" + countryName);
            }
            String finalCountryName = countryName;
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                int count = 0;

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int i = 0;
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            Boolean is_for_adults_only = eventSnapshot.child("age_restricted").getValue(Boolean.class);
                            eventRestriction.add(is_for_adults_only);

                            if(!(is_for_adults_only == true && display_adult_content == false)){
                                String eventLocalization = eventSnapshot.child("event_localization").getValue(String.class);
                                eventLocalizationAll.add(eventLocalization);


                                List<Address> addressList = null;
                                Address address = null;

                                try {
                                    addressList = geocoder.getFromLocationName(eventLocalizationAll.get(i), 1);
                                } catch (IllegalArgumentException | IOException e) {
                                    Toast.makeText(getApplicationContext(), failed_location.getText().toString(), Toast.LENGTH_LONG).show();
                                }
                                address = addressList.get(0);
                                double distance = Math.round(calculateDistance(current_lat, current_lng, address.getLatitude(), address.getLongitude()));
                                if(distance <= scan_radius){
                                    String eventName = eventSnapshot.child("event_name").getValue(String.class);
                                    eventNameV.add(eventName);

                                    eventLocalizationV.add(eventLocalizationAll.get(i));


                                    Date eventDuration = eventSnapshot.child("event_duration").getValue(Date.class);
                                    eventDateV.add(eventDuration);

                                    String eventDateTime = eventSnapshot.child("time_and_date").getValue(String.class);
                                    eventDateAndTimeV.add(eventDateTime);

                                    String eventOrganizer = eventSnapshot.child("organizer").getValue(String.class);
                                    eventOrganizerV.add(eventOrganizer);
                                    near_events_number++;

                                }

                                i++;
                            }


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

                                if(social_mode){
                                    reference = FirebaseDatabase.getInstance().getReference("SocialEvent/" + finalCountryName).child(ref);

                                }
                                else{
                                    reference = FirebaseDatabase.getInstance().getReference("Event/" + finalCountryName).child(ref);
                                }
                                reference.removeValue();


                                final DatabaseReference[] reference = {FirebaseDatabase.getInstance().getReference("CompanyEmails")};
                                String email = eventOrganizerV.get(j);
                                final String[] email_date_and_time = new String[1];
                                reference[0].addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot companySnapshot : snapshot.getChildren()) {
                                            String emailDB = companySnapshot.child("email").getValue(String.class);
                                            if (emailDB.equals(email)) {
                                                email_date_and_time[0] = companySnapshot.getKey();

                                                if(social_mode){
                                                    reference[0] = database.getReference("CompanyEmails/" + email_date_and_time[0] + "/CompanySocialEvent");

                                                }
                                                else{
                                                    reference[0] = database.getReference("CompanyEmails/" + email_date_and_time[0] + "/CompanyEvent");
                                                }
                                                reference[0].removeValue();
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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
                        Toast.makeText(getApplicationContext(), no_events_found.getText().toString(), Toast.LENGTH_LONG).show();
                        scan_button_animation = false;
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

    void showMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Sprawdź uprawnienia
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Brak uprawnień, obsłuż odpowiednio
            return;
        }

        try {
            // Spróbuj uzyskać lokalizację z NETWORK_PROVIDER
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location == null) {
                // Jeżeli nie udało się uzyskać lokalizacji z NETWORK_PROVIDER, spróbuj z GPS_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location != null) {
                // Jeżeli udało się uzyskać lokalizację, dodaj marker na mapie
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.localization_tracker)).title(String.valueOf(your_localization.getText())));

                assert marker != null;
                current_lat = marker.getPosition().latitude;
                current_lng = marker.getPosition().longitude;
            } else {
                // Jeżeli nie udało się uzyskać lokalizacji, przejdź do EnableLocalization
                if (activity_intent.getStringExtra("activity").equals("user")) {
                    Intent intent = new Intent(MapActivityMain.this, EnableLocalization.class);
                    intent.putExtra("activity", "user");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MapActivityMain.this, EnableLocalization.class);
                    intent.putExtra("activity", "main");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }

        } catch (SecurityException e) {
            // Obsługa błędu związanego z uprawnieniami
        }
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
                        eventActivity.putExtra("Localization", eventLocalizationV.get(markerIndex));

                        if(eventDateV.get(markerIndex).getMinutes() < 10){
                            eventActivity.putExtra("Duration", eventDateV.get(markerIndex).getHours() + ":0" + eventDateV.get(markerIndex).getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                        }
                        else{
                            eventActivity.putExtra("Duration", eventDateV.get(markerIndex).getHours() + ":" + eventDateV.get(markerIndex).getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                        }
                        eventActivity.putExtra("DateAndTime", eventDateAndTimeV.get(markerIndex));
                        eventActivity.putExtra("Country", countryName);
                        eventActivity.putExtra("isavailable", "true");
                        eventActivity.putExtra("organizer", eventOrganizerV.get(markerIndex));
                        eventActivity.putExtra("restricted", eventRestriction.get(markerIndex));

                        if(social_mode){
                            eventActivity.putExtra("social_mode", "true");
                        }
                        else{
                            eventActivity.putExtra("social_mode", "false");
                        }

                        if(activity_intent.getStringExtra("activity").equals("user")){
                            eventActivity.putExtra("activity", "user");
                        }
                        else{
                            eventActivity.putExtra("activity", "main");
                        }


                        startActivity(eventActivity);
                    } else {
                        //  Toast.makeText(getApplicationContext(), "Jesteś za daleko", Toast.LENGTH_LONG).show();


                        int markerIndex = (int) markerr.getTag();
                        Intent eventActivity = new Intent(MapActivityMain.this, EventActivity.class);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(eventDateV.get(markerIndex));

                        int month = calendar.get(Calendar.MONTH) + 1;

                        eventActivity.putExtra("Name", eventNameV.get(markerIndex));
                        //  eventActivity.putExtra("Description", eventDescV.get(markerIndex));
                        eventActivity.putExtra("Localization", eventLocalizationV.get(markerIndex));
                        //eventActivity.putExtra("Company", eventCompanyNameV.get(markerIndex));
                        if(eventDateV.get(markerIndex).getMinutes() < 10){
                            eventActivity.putExtra("Duration", eventDateV.get(markerIndex).getHours() + ":" + eventDateV.get(markerIndex).getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                        }
                        else{
                            eventActivity.putExtra("Duration", eventDateV.get(markerIndex).getHours() + ":" + eventDateV.get(markerIndex).getMinutes() + " " + calendar.get(Calendar.DAY_OF_MONTH) + "." + month + "." + calendar.get(Calendar.YEAR));
                        }
                        // eventActivity.putExtra("Additional", eventAdditionalV.get(markerIndex));
                        eventActivity.putExtra("DateAndTime", eventDateAndTimeV.get(markerIndex));
                        eventActivity.putExtra("Country", countryName);
                        eventActivity.putExtra("isavailable", "false");

                        if(social_mode){
                            eventActivity.putExtra("social_mode", "true");
                        }
                        else{
                            eventActivity.putExtra("social_mode", "false");
                        }

                        if(activity_intent.getStringExtra("activity").equals("user")){
                            eventActivity.putExtra("activity", "user");
                        }
                        else{
                            eventActivity.putExtra("activity", "main");
                        }


                        startActivity(eventActivity);
                    }
                });
            }
        });

        can_scan_events = true;
        scan_button_animation = false;
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
                        try{
                            near_events[finalI] = mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(eventNameV.get(finalI))).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ahoylocalpin)));
                            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinezkalokalna)));
                            near_events[finalI].setTag(finalI);
                        }
                        catch (IndexOutOfBoundsException e){
                            Toast.makeText(getApplicationContext(), something_went_wrong.getText().toString() , Toast.LENGTH_LONG).show();
                        }



                    }
                });
            }
        }
        can_be_deleted_scan = true;

        markerOnClick();
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void scanButtonAnimation(){
        final boolean[] isFadeIn = {true};
        AlphaAnimation fadeIn = new AlphaAnimation(0.5f, 1.0f);
        fadeIn.setDuration(800);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.5f);
        fadeOut.setDuration(800);


        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(fadeIn);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                isFadeIn[0] = !isFadeIn[0];
                if(scan_button_animation){
                    scanButtonAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


        scan_button.startAnimation(animationSet);
    }

    private void downloadingDataAnimation(){
        final boolean[] isFadeIn = {true};
        AlphaAnimation fadeIn = new AlphaAnimation(0.5f, 1.0f);
        fadeIn.setDuration(800);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.5f);
        fadeOut.setDuration(800);


        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(fadeIn);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                isFadeIn[0] = !isFadeIn[0];
                if(downloading_data_animation){
                    downloadingDataAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


        downloadingdata.startAnimation(animationSet);
    }


    public void fetchDateAndScanEvents() {
        OnlineDate.fetchDateAsync(this);
    }

    @Override
    public void onDateFetched(Date date) {
        if (date != null) {
            try {
                this.date = OnlineDate.getDate();
                scanEvents();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
