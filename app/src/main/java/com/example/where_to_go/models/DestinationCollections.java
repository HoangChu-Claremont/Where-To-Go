package com.example.where_to_go.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Tours")
public class DestinationCollections extends ParseObject {

    public static final String TOUR_NAME = "tour_name";
    public static final String TRANSPORTATION_SECONDS = "transportation_seconds";
    public static final String USER_ID = "user_id";

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

    // SETTER
    public void setTourName(String tourName) {
        put(TOUR_NAME, tourName);
    }

    public void setTransportationSeconds(double transportationSeconds) {
        put(TRANSPORTATION_SECONDS, transportationSeconds);
    }

    public void setUserId(String user) {
        put(USER_ID, user);
    }
}
