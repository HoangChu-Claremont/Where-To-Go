package com.example.where_to_go.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Tours")
public class Tours extends ParseObject {

    public static final String TOUR_NAME = "tour_name";
    public static final String TRANSPORTATION_SECONDS = "transportation_seconds";
    public static final String USER_ID = "user_id";
    public static final String IS_SAVED = "isSaved";
    public static final String OBJECT_ID = "objectId";

    // GETTER
    public String getTourName() {
        return getString(TOUR_NAME);
    }

    public String getTransportationSeconds() {
        return getString(TRANSPORTATION_SECONDS);
    }

    public ParseUser getUserId() {
        return getParseUser(USER_ID);
    }

    public boolean getSaved() {
        return getBoolean(IS_SAVED);
    }

    // SETTER
    public void setTourName(String tourName) {
        put(TOUR_NAME, tourName);
    }

    public void setTransportationSeconds(double transportationSeconds) {
        put(TRANSPORTATION_SECONDS, transportationSeconds);
    }

    public void setIsSaved(boolean isSaved) {
        put(IS_SAVED, isSaved);
    }
}
