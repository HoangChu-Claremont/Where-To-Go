package com.example.where_to_go.models;

import androidx.annotation.NonNull;

import com.example.where_to_go.utilities.RatingComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Destinations {
    // TODO: Add more relevant fields
    private double longitude, latitude;
    private double rating;
    private String title, imageUrl;

    public Destinations(@NonNull JSONObject jsonObject) throws JSONException {
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

    public void setTitle(JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString("name");
    }

    public void setImageUrl(JSONObject jsonObject) throws JSONException {
        imageUrl = jsonObject.getString("image_url");
    }

}
