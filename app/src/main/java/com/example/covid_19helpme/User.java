package com.example.covid_19helpme;

import android.location.Location;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class User implements Serializable {
    private String name;
    private double longitude;
    private double latitude;
    private String blurb;
    private Location location;

    private ArrayList<String> family;
    private int tag;
    private HashMap<String,ArrayList<String>> touched;

    private ArrayList<String> cases;

    public User(String name, String blurb) {
        this.name = name;
        this.blurb=blurb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<String> getFamily() {
        return family;
    }

    public void setFamily(ArrayList<String> family) {
        this.family = family;
    }

    public void addMember(String blurb){
        this.family.add(blurb);
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public HashMap<String,ArrayList<String>> getTouched() {
        return touched;
    }

    public void setTouched(HashMap<String,ArrayList<String>> touched) {
        this.touched = touched;
    }

    public ArrayList<String> getCases() {
        return cases;
    }

    public void setCases(ArrayList<String> cases) {
        this.cases = cases;
    }

    public ArrayList<Location> getNearby(ArrayList<Location> latlongList) {
        ArrayList<Location> tooClose = new ArrayList<>();
        Location location = new Location("a");
        location.setLatitude(this.latitude);
        location.setLongitude(this.longitude);
        for (Location loc :latlongList) {
            double distance = (location.distanceTo(loc))*3.28083333333;
            Log.i("distance", Double.toString(distance)+loc);
            if (distance <=7){
                tooClose.add(loc);
            }
        }
        Log.i("too close", tooClose.toString());
        return tooClose;
    }
}
