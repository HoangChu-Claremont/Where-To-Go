package com.example.where_to_go.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Tours")
public class Tour extends ParseObject {

    public static final String TOUR_NAME = "tour_name";
    public static final String TRANSPORTATION_SECONDS = "transportation_seconds";
    public static final String USER_ID = "user_id";
    public static final String IS_SAVED = "isSaved";
    public static final String IS_FEATURED = "isFeatured";

    // GETTER
    public String getTourNameDB() {
        return getString(TOUR_NAME);
    }

    public String getTransportationSecondsDB() {
        return getString(TRANSPORTATION_SECONDS);
    }

    public ParseUser getUserIdDB() {
        return getParseUser(USER_ID);
    }

    public boolean getIsSavedDB() {
        return getBoolean(IS_SAVED);
    }

    // SETTER
    public void setTourNameDB(String tourName) {
        put(TOUR_NAME, tourName);
    }

    public void setTransportationSecondsDB(double transportationSeconds) {
        put(TRANSPORTATION_SECONDS, transportationSeconds);
    }

    public void setIsSavedDB(boolean isSaved) {
        put(IS_SAVED, isSaved);
    }
}
