package com.example.where_to_go.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Path{

    // For testing only
    public final String TOP_RATED = "Top Rated";
    public final String IMAGE_URL = "https://i.imgur.com/a/xv2bPub.jpeg";

    private double longitude, latitude;
    private double rating;

    public Path (@NonNull JSONObject jsonObject) throws JSONException {
        JSONObject coordinates = jsonObject.getJSONObject("coordinates");

        longitude = coordinates.getDouble("longitude");
        latitude = coordinates.getDouble("latitude");
        rating = jsonObject.getDouble("rating");
    }

    @NonNull
    public static List<Path> getTopRatedPath(@NonNull JSONArray businesses) throws JSONException {
        List<Path> paths = new ArrayList<>();
        for (int pos = 0; pos < businesses.length(); ++pos) {
            paths.add(new Path(businesses.getJSONObject(pos)));
        }

        Collections.sort(paths, new RatingComparator());

        return paths;
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
}
