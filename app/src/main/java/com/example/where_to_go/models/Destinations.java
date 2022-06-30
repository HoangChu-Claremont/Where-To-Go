package com.example.where_to_go.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.where_to_go.utilities.RatingComparator;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ParseClassName("Destinations")
public class Destinations extends ParseObject {
    private static final String LOCATION_NAME = "location_name";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String RATING = "rating";
    private static final String IMAGE_URL = "location_image_url";
    private static final String TAG = "Destinations";

    // TODO: Add more relevant fields
    private double longitude, latitude;
    private double rating;
    private String title, imageUrl;

    public Destinations() {}

    public void setData(@NonNull JSONObject jsonObject) throws JSONException {
        setCoordinate(jsonObject);
        setRating(jsonObject);
        setTitle(jsonObject);
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

    public String getTitle() {
        return title;
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

    private void setTitle(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("name");
    }

    private void setImageUrl(JSONObject jsonObject) throws JSONException {
        imageUrl = jsonObject.getString("image_url");
    }

    public void putToDB() {
        put(LONGITUDE, longitude);
        put(LATITUDE, latitude);
        put(RATING, rating);
        put(LOCATION_NAME, title);
        put(IMAGE_URL, imageUrl);
    }
}
