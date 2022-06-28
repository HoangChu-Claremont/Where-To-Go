package com.example.where_to_go.models;

import androidx.annotation.NonNull;

import com.example.where_to_go.utilities.RatingComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Destination {
    // TODO: Add more relevant fields
    private double longitude, latitude;
    private double rating;
    private String title, imageUrl;

    String PATH_TYPE_IMAGE_URL = "http://via.placeholder.com/300.png";

    public Destination(@NonNull JSONObject jsonObject) throws JSONException {
        setCoordinate(jsonObject);
        setRating(jsonObject);
        setTitle(jsonObject);
        setImageUrl(PATH_TYPE_IMAGE_URL);
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

    public void setImageUrl(String _imageUrl) {
        imageUrl = _imageUrl;
    }

}
