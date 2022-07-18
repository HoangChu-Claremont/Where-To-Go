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

    public static final String LOCATION_NAME = "location_name";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String IMAGE_URL = "location_image_url";
    public static final String RATING = "rating";
    public static final String TOUR_ID = "tour_id";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final double METERS_TO_MILES = 0.00062137;
    public static final double CANNOT_FIND_DISTANCE = -1.0;

    private double longitude, latitude, distance;
    private double rating;
    private String locationName;
    private String imageUrl;
    private String phone;
    private String address;
    private String yelpId;

    public Destination() {
        // empty constructor required by the Parceler library
    };

    public void setData(@NonNull JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "setData");

        setCoordinate(jsonObject);
        setRating(jsonObject);
        setLocationName(jsonObject);
        setImageUrl(jsonObject);
        setPhone(jsonObject);
        setAddress(jsonObject);
        setDistanceFromDeviceLocation(jsonObject);
        setYelpID(jsonObject);
    }

    // GETTER
    public String getYelpId() {
        return yelpId;
    }

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

    public String getIdDB() {
        return this.getObjectId();
    }

    public String getAddressDB() {
        return getString(ADDRESS);
    }

    public double getRatingDB() {
        return getDouble(RATING);
    }

    public double getLongitudeDB() {
        return getDouble(LONGITUDE);
    }

    public double getLatitudeDB() {
        return getDouble(LATITUDE);
    }

    public String getLocationNameDB() {
        return getString(LOCATION_NAME);
    }

    public String getImageUrlDB() {
        return getString(IMAGE_URL);
    }

    public String getPhoneDB() {
        return getString(PHONE);
    }

    public void getFieldFromDB(){
        Log.i(TAG, "getFieldFromDB");

        address = getAddressDB();
        rating = getRatingDB();
        longitude = getLongitudeDB();
        latitude = getLatitudeDB();
        locationName = getLocationNameDB();
        imageUrl = getImageUrlDB();
        phone = getPhoneDB();
    }

    // SETTER

    private void setYelpID(@NonNull JSONObject jsonObject) throws JSONException {
        yelpId = jsonObject.getString("id");
    }

    private void setAddress(@NonNull JSONObject jsonObject) throws JSONException {
        JSONArray addresses = jsonObject.getJSONObject("location").getJSONArray("display_address");
        String street = "";
        String county = "";

        if (addresses.length() >= 1) { // There exists a street name in the array
            street = addresses.getString(0);
        }

        if (addresses.length() > 1) { // There exists a county name in the array
            county = addresses.getString(1);
        }

        address = street + ", " + county;
    }

    private void setDistanceFromDeviceLocation(@NonNull JSONObject jsonObject) {
        Log.i(TAG, "setDistanceFromDeviceLocation");

        double distanceInMeter, distanceInMiles;

        try {
            distanceInMeter = jsonObject.getDouble("distance");
            distanceInMiles = convertMetersToMiles(distanceInMeter);
        } catch (JSONException e){
            distanceInMiles = CANNOT_FIND_DISTANCE;
            Log.i(TAG, "Error getting distance: " + e.getMessage());
        }

        distance = roundToOneDecimalValue(distanceInMiles);
    }

    public double setCustomDistance(double origLongitude, double origLatitude) {
        Log.i(TAG, "setCustomDistance");

        double destinationLongitude = getLongitude();
        double destinationLatitude = getLatitude();

        return Math.sqrt((destinationLongitude - origLongitude) * (destinationLatitude - origLongitude) +
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
        Log.i(TAG, "putToDB");

        put(LOCATION_NAME, locationName);
        put(LONGITUDE, longitude);
        put(LATITUDE, latitude);
        put(IMAGE_URL, imageUrl);
        put(RATING, rating);
        put(PHONE, phone);
        put(ADDRESS, address);
    }

    // HELPER METHODS
    private double roundToOneDecimalValue(double distanceInMiles) {
        Log.i(TAG, "roundToOneDecimalValue");

        return Math.round(distanceInMiles * 10.0) / 10.0;
    }

    private double convertMetersToMiles(double distanceInMeter) {
        Log.i(TAG, "convertMetersToMiles");

        return distanceInMeter * METERS_TO_MILES;
    }
}
