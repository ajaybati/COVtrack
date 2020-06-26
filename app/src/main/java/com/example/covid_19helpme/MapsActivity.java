package com.example.covid_19helpme;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;



public class MapsActivity extends FragmentActivity implements GoogleMap.OnCameraMoveStartedListener,OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Button whereAmI;
    Button familyMember;
    User user_app;
    DatabaseReference dref;
    List<ArrayList<Double>> latLongList;
    int tag;
    boolean add = false;
    ArrayList<String> myfamily;
    HashMap<String,ArrayList<String>> touched;
    ArrayList<String> selected;




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }

            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        familyMember=findViewById(R.id.familyMember);
        whereAmI = findViewById(R.id.button1);
        whereAmI.setAlpha(0);
        Intent intent = getIntent();
        user_app = (User) intent.getSerializableExtra("name");
        selected = intent.getStringArrayListExtra("cases");
        if(selected!=null){
            user_app.setCases(selected);
        }
        dref = FirebaseDatabase.getInstance().getReference("Users");
        if(user_app.getFamily() == null){
            myfamily = new ArrayList<>();
        }
        else {
            myfamily = user_app.getFamily();
        }

        if(user_app.getTouched() == null){
            touched = new HashMap<String, ArrayList<String>>();
        }
        else {
            touched = user_app.getTouched();
            if(user_app.getCases()!=null) {
                Log.i("submit case", user_app.getCases().toString());
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                whereAmI.animate().alpha(0).setDuration(500).start();
                familyMember.animate().alpha(0).setDuration(500).start();
                user_app.setLongitude(location.getLongitude());
                user_app.setLatitude(location.getLatitude());
//                user_app.setLocation(location);

                dref.child((String) user_app.getBlurb()).setValue(user_app);
                latLongList = new ArrayList<ArrayList<Double>>();


                dref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mMap.clear();
                        int theCount = 0;
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            if(selected!=null && snapshot.child("cases").getValue()!=null){
                                Log.i("isitdoing",snapshot.child("cases").getValue().toString()+snapshot.child("blurb").getValue().toString());
                            }
                            if (add) { //family code
                                if (theCount == user_app.getTag()) {
                                    if (user_app.getFamily()==null) {
                                        myfamily.add((String) snapshot.child("blurb").getValue());
                                        user_app.setFamily(myfamily); //instead of making a datasnapshot list, make a strings list in user.java with the blurb of this snapshot so I can access the lat and long of this family member and comparing it to the lat and long in the nearby list that I create later on; if location from snapshot is in the nearby list, that snapshot is a family member so i don't need to say be careful
                                        Log.i("family", user_app.getFamily().toString());
                                        add = false;
                                    }
                                    else if(!user_app.getFamily().contains((String) snapshot.child("blurb").getValue())){
                                        myfamily.add((String) snapshot.child("blurb").getValue());
                                        user_app.setFamily(myfamily);
                                        Log.i("family", user_app.getFamily().toString());
                                        add = false;
                                    }
                                }
                            }
                            if (snapshot.child("cases").getValue()!=null && snapshot.child("blurb").getValue()!=user_app.getBlurb()){
                                for(String person: touched.keySet()){
                                    if(person.equals((String) snapshot.child("blurb").getValue())){
                                        Toast.makeText(MapsActivity.this,"u shud be scared to death"+touched.get(person).toString(),Toast.LENGTH_LONG).show();
                                    }
                                }

                            }
                            theCount++;
                            ArrayList<Double> position = new ArrayList<>();
                            double latitude, longitude;
                            latitude = (double) snapshot.child("latitude").getValue();
                            longitude = (double) snapshot.child("longitude").getValue();

                            position.add(latitude);
                            position.add(longitude);
                            latLongList.add(position);
                        }
                        ArrayList<Location> latlonglistCHECK = new ArrayList<>();
                        int count=0;
                        for (ArrayList<Double> latlng: latLongList) {
                            LatLng pos = new LatLng(latlng.get(0), latlng.get(1));

                            Location comparison = new Location("location");
                            comparison.setLatitude(pos.latitude);
                            comparison.setLongitude(pos.longitude);
                            latlonglistCHECK.add(comparison);
                            if(latlng.get(0)==user_app.getLatitude() && latlng.get(1)==user_app.getLongitude()) {
                                mMap.addMarker(new MarkerOptions().position(pos).title("Your loc")).setTag(count);
                            }
                            else{
                                mMap.addMarker(new MarkerOptions().position(pos).title("Another person")).setTag(count);
                            }
                            count++;

                        }
                        ArrayList<Location> nearby = user_app.getNearby(latlonglistCHECK);
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (nearby.size() > 0) {
                                int a = 0;
                                for(int x=0;x<nearby.size();x++) {
                                    if (myfamily.contains(snapshot.child("blurb").getValue())) {
                                        if (nearby.get(a).getLongitude() == (double) snapshot.child("longitude").getValue() && nearby.get(a).getLatitude() == (double) snapshot.child("latitude").getValue()) {
                                            nearby.remove(a);
                                        }
                                    }
                                    if (nearby.get(a).getLongitude() == (double) snapshot.child("longitude").getValue() && nearby.get(a).getLatitude() == (double) snapshot.child("latitude").getValue()) {
                                        Set<String> keySet = touched.keySet();
                                        ArrayList<String> touch=new ArrayList<>(keySet);

                                        if(!touch.contains(snapshot.child("blurb").getValue())) {
                                            LocalDateTime myDate = LocalDateTime.now();
                                            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                                            String formattedDate = myDate.format(myFormatObj);
                                            ArrayList<String> dates = new ArrayList<>();
                                            dates.add(formattedDate);
                                            touched.put((String) snapshot.child("blurb").getValue(),dates);
                                            touched.remove(user_app.getBlurb());
                                            user_app.setTouched(touched);
                                        }
//                                        else{
//                                            LocalDateTime myDate = LocalDateTime.now();
//                                            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                                            String formattedDate = myDate.format(myFormatObj);
//                                            touched.get((String) snapshot.child("blurb").getValue()).add(formattedDate);
//                                        }
                                    }
                                    a++;
                                }
                            }
                        }

                        if(nearby.size() >1){
                            if(nearby.get(0).getLatitude()!=nearby.get(1).getLatitude() || nearby.get(0).getLongitude()!=nearby.get(1).getLongitude()) {
                                Toast.makeText(MapsActivity.this, "You are too close to someone. Be careful!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                CameraPosition next = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(20).bearing(300).tilt(50).build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(next));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastknown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastknown!=null) {
                    Log.i("last knowm", lastknown.toString());
                    LatLng somewhere = new LatLng(lastknown.getLatitude(), lastknown.getLongitude());


                    user_app.setLatitude(lastknown.getLatitude());
                    user_app.setLongitude(lastknown.getLongitude());
                    dref.child(user_app.getBlurb()).setValue(user_app);
                    Log.i("latitude last known", Double.toString(user_app.getLatitude()));

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(somewhere).title("Your loc").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(somewhere));

                    CameraPosition next = new CameraPosition.Builder().target(somewhere).zoom(20).bearing(300).tilt(50).build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(next));
                }
            }
        }
    }


    @Override
    public void onCameraMoveStarted(int i) {
        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            whereAmI.animate().alpha(1).setDuration(500).start();
            locationManager.removeUpdates(locationListener);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                tag = (Integer) marker.getTag();
                familyMember.animate().alpha(1).setDuration(500).start();
                return true;
            }
        });
    }

    public void reset(View view){
        LatLng userlocation = new LatLng(user_app.getLatitude(), user_app.getLongitude());
        CameraPosition next = new CameraPosition.Builder().target(userlocation).zoom(20).bearing(300).tilt(50).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(next));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void addMember(View view){
        add = true;
        user_app.setTag(tag);
        Log.i("tag",Integer.toString(tag));
        Toast.makeText(MapsActivity.this, "Added family member", Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void submitCase(View view){

        Intent intent = new Intent(getApplicationContext(), submitCOVIDcase.class);
        intent.putExtra("name",user_app);
        startActivity(intent);
    }

//    public void getLatLong(){
//
//    }

//
//    @Override
//    public void onCameraMoveCanceled() {
//
//    }
//
//    @Override
//    public void onCameraMove() {
//
//    }
}
