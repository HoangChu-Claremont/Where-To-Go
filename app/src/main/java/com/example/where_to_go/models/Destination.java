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

    private double longitude, latitude;

    private double rating;

    public Destination(@NonNull JSONObject jsonObject) throws JSONException {
        setCoordinate(jsonObject);
        setRating(jsonObject);
    }

    // GETTER

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getRating() {
        return rating;
    }

    // SETTER

    private void setCoordinate(JSONObject jsonObject) throws JSONException {
        JSONObject coordinates = jsonObject.getJSONObject("coordinates");
        longitude = coordinates.getDouble("longitude");
        latitude = coordinates.getDouble("latitude");
    }

    private void setRating(JSONObject jsonObject) throws JSONException {
        rating = jsonObject.getDouble("rating");
    }

}
