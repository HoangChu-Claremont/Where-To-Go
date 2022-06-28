package com.example.where_to_go.utilities;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.where_to_go.models.Destination;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterAlgorithm {

    private String intent;

    public FilterAlgorithm(String _intent) {
        intent = _intent;
    }

    @NonNull
    public static List<Destination> getTopRatedPath(@NonNull JSONArray businesses) throws JSONException {
        List<Destination> destinations = new ArrayList<>();
        for (int pos = 0; pos < businesses.length(); ++pos) {
            destinations.add(new Destination(businesses.getJSONObject(pos)));
        }

        Collections.sort(destinations, new RatingComparator());

        return destinations;
    }
}
