package com.example.where_to_go.models;

import androidx.annotation.NonNull;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("Destinations")
public class Destination extends ParseObject {
    private static final String LOCATION_NAME = "location_name";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String IMAGE_URL = "location_image_url";
    private static final String RATING = "rating";

    private double longitude, latitude;
    private double rating;
    private String locationName, imageUrl;

    public void setData(@NonNull JSONObject jsonObject) throws JSONException {
        setCoordinate(jsonObject);
        setRating(jsonObject);
        setLocationName(jsonObject);
        setImageUrl(jsonObject);
    }

    // GETTER

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

    // SETTER

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
