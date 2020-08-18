package com.example.eslam.bumpdetector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.icu.text.DecimalFormat;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eslam on 5/15/2020.
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.OnConnectionFailedListener{

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
            addbumpslocations(mMap);
        }
    }

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 138));
    private double[] latitudes =  {31.1069964, 31.1089094, 31.115897, 31.121502, 31.130745};
    private double[] longitudes = {29.7829112, 29.785704 , 29.795583, 29.786040, 29.783033};
    private LatLng nearbump = new LatLng(0,0);   //to save the near bump
    private Location currentLocation;                   //my current location
    private String currentStreet;                       //my current street name
    private int flag = 0;                               //flag used for id of notification and have 4 stages
                                                            // 0 -> Start state
                                                            // 1 -> looking for a near bump
                                                            // 2 -> when my current location is equal to near bump
                                                            // 3 -> return back for looking for next near bump and so on

    private Handler mHandler = new Handler();
    private Handler mHandler1 = new Handler();


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;
    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        getLocationPermission();

    }

    private void init(){
        Log.d(TAG, "init: initializing");


        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Click gps icon");
                getDeviceLocation();
                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                        DEFAULT_ZOOM,
                        "My Location");
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geoLocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location " + address.toString());
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: Getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();
                            if(flag==0)
                            {
                                flag++;
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM,
                                        "My Location");
                                StartRepeating();

                            }
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                mLocationPermissionGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void addbumpslocations(GoogleMap mMap){
        LatLng bump;
        for(int count = 0; count <latitudes.length; count++){
            bump = new LatLng(latitudes[count], longitudes[count]);
            mMap.addMarker(new MarkerOptions()
                    .position(bump)
                    .title("Bump")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bump_notice)));
        }
    }

    public void calDis(double curlat, double curlongt){
        int Radius = 6371;      //radius of earth in Km
        double lat1 = curlat;   //latitude of my current location
        double lon1 = curlongt; //longitude of my current location
        double lat2;            //latitude of bump
        double lon2;            //longitude of bump
        double dLat;            //difference between two latitudes
        double dLon;            //difference between two longitudes
        double a;
        double c;
        double valueResult;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float km;               //distance in Km
        int meter;              //distance in meter
        for(int count = 0; count < latitudes.length; count++){
            lat2 = latitudes[count];
            lon2 = longitudes[count];
            if((lat2 == nearbump.latitude && lon2 == nearbump.longitude)){
                continue;
            }
            dLat = Math.toRadians(lat2 - lat1);
            dLon = Math.toRadians(lon2 - lon1);
            a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            c = 2 * Math.asin(Math.sqrt(a));
            valueResult = Radius * c;
            km = Float.valueOf(decimalFormat.format(valueResult));
            meter = (int)(km * 1000.0);
            if(meter <= 300){
                nearbump = new LatLng(lat2,lon2);
                flag++;
                sendNotification(meter);
                break;
            }
        }
    }

    private void StartRepeating(){
        mToastRunnable.run();
    }

    private void StopRepeating(){
        mHandler.removeCallbacks(mToastRunnable);
    }

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            calDis(currentLocation.getLatitude(), currentLocation.getLongitude());
            getDeviceLocation();
            mHandler.postDelayed(this,5000);
            if(flag==2){
                StopRepeating();
                StartRepeatingNear();
            }
        }
    };

    private void sendNotification(int distance){
        String message = "There is a bump after " + distance + " meter";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this)
                .setSmallIcon(R.drawable.notification)
                .setContentText(message)
                .setContentTitle("Bump Detector")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setColor(0x2196F3);  //Light Blue
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MapsActivity.this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(flag, builder.build());
    }

    private void StartRepeatingNear(){
        mToastRunnable1.run();
    }

    private void StopRepeatingNear(){
        mHandler1.removeCallbacks(mToastRunnable1);
    }


    private Runnable mToastRunnable1 = new Runnable() {
        @Override
        public void run() {
            calDis2(currentLocation.getLatitude(), currentLocation.getLongitude(), nearbump);
            getDeviceLocation();
            mHandler1.postDelayed(this,5000);
            if(flag==3)
            {
                flag = 1;
                StopRepeatingNear();
                StartRepeating();
            }
        }
    };

    public void calDis2(double curlat, double curlongt, LatLng bump){
        int Radius = 6371;// radius of earth in Km
        double lat1 = bump.latitude;
        double lat2 = curlat;
        double lon1 = bump.longitude;
        double lon2 = curlongt;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float km = Float.valueOf(decimalFormat.format(valueResult));
        int meter = (int)(km * 1000.0);
        if(meter < 10 || meter > 300){
            flag++;
        }
    }

    private String getStreetName(double lat, double lon){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String streetName = addresses.get(0).getThoroughfare();
            Toast.makeText(this, "Street Name = " + streetName, Toast.LENGTH_SHORT).show();
            return streetName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
