package com.example.where_to_go.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@ParseClassName("Destinations")
@Parcel(analyze = Destination.class)
public class Destination extends ParseObject {
    private static final String LOCATION_NAME = "location_name";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String IMAGE_URL = "location_image_url";
    private static final String RATING = "rating";
    private static final String TAG = "Destination";

    public double longitude, latitude;
    public double rating;
    public String locationName, imageUrl;

    public Destination() {
        // empty constructor required by the Parceler library
    };

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
        try {
            JSONObject coordinates = jsonObject.getJSONObject("coordinates");
            longitude = coordinates.getDouble("longitude");
            latitude = coordinates.getDouble("latitude");
        } catch (Exception e) {
            longitude = jsonObject.getDouble("longitude");
            latitude = jsonObject.getDouble("latitude");
        }
    }

    private void setRating(@NonNull JSONObject jsonObject) throws JSONException {
        rating = jsonObject.getDouble("rating");
    }

    public void setLocationName(@NonNull JSONObject jsonObject) throws JSONException {
        try {
            locationName = jsonObject.getString("name");
        } catch (Exception e) {
            locationName = jsonObject.getString("locationName");
        }
    }

    public void setImageUrl(@NonNull JSONObject jsonObject) throws JSONException {
        try {
            imageUrl = jsonObject.getString("image_url");
        } catch (Exception e) {
            imageUrl = jsonObject.getString("imageUrl");
        }
    }

    public void putToDB() {
        put(LOCATION_NAME, locationName);
        put(LONGITUDE, longitude);
        put(LATITUDE, latitude);
        put(IMAGE_URL, imageUrl);
        put(RATING, rating);
    }
}
