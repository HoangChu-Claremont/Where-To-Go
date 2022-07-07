package com.example.where_to_go.models;


import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("Destinations")
public class Destination extends ParseObject {
    private static final String TAG = "Destination";

    private static final String LOCATION_NAME = "location_name";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String IMAGE_URL = "location_image_url";
    private static final String RATING = "rating";

    private double longitude, latitude, distance;
    private double rating;
    private String locationName;
    private String imageUrl;
    private String phone;
    private String address;

    public Destination() {
        // empty constructor required by the Parceler library
    };

    public void setData(@NonNull JSONObject jsonObject) throws JSONException {
        setCoordinate(jsonObject);
        setRating(jsonObject);
        setLocationName(jsonObject);
        setImageUrl(jsonObject);
        setPhone(jsonObject);
        setAddress(jsonObject);
    }

    // GETTER

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public double getDistance() {
        return distance;
    }

    // SETTER

    private void setAddress(@NonNull JSONObject jsonObject) throws JSONException {
        JSONArray addresses = jsonObject.getJSONObject("location").getJSONArray("display_address");
        String street = "";
        String county = "";
        if (addresses.length() >= 1) {
            street = addresses.getString(0);
        }
        if (addresses.length() > 1) {
            county = addresses.getString(1);
        }
        address = street + ", " + county;
    }

    public void setDistance(double origLongitude, double origLatitude) {
        double destinationLongitude = getLongitude();
        double destinationLatitude = getLatitude();
        distance = Math.sqrt((destinationLongitude - origLongitude) * (destinationLatitude - origLongitude) +
                (destinationLatitude - origLatitude) * (destinationLatitude - origLatitude));
    }

    private void setPhone(@NonNull JSONObject jsonObject) throws JSONException {
        phone = jsonObject.getString("display_phone");
    }

    private void setCoordinate(@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject coordinates = jsonObject.getJSONObject("coordinates");
        longitude = coordinates.getDouble("longitude");
        latitude = coordinates.getDouble("latitude");
    }

    private void setRating(@NonNull JSONObject jsonObject) throws JSONException {
        rating = jsonObject.getDouble("rating");
    }

    public void setLocationName(@NonNull JSONObject jsonObject) throws JSONException {
        locationName = jsonObject.getString("name");
    }

    public void setImageUrl(@NonNull JSONObject jsonObject) throws JSONException {
        imageUrl = jsonObject.getString("image_url");
    }

    public void putToDB() {
        put(LOCATION_NAME, locationName);
        put(LONGITUDE, longitude);
        put(LATITUDE, latitude);
        put(IMAGE_URL, imageUrl);
        put(RATING, rating);
    }
}
