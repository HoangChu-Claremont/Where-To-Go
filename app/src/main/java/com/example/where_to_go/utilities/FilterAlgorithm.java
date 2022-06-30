package com.example.where_to_go.utilities;

import androidx.annotation.NonNull;

import com.example.where_to_go.models.Destinations;

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
    public static List<Destinations> getTopRatedTour(@NonNull JSONArray businesses) throws JSONException {
        List<Destinations> outputDestinations = new ArrayList<>();
        List<Destinations> inputDestinations = new ArrayList<>();
        for (int pos = 0; pos < businesses.length(); ++pos) {
            Destinations destinations = new Destinations();
            destinations.setData(businesses.getJSONObject(pos));
            inputDestinations.add(destinations);
        }

        Collections.sort(inputDestinations, new RatingComparator());

        for (int pos = 0; pos < 10; ++pos) {
            outputDestinations.add(inputDestinations.get(pos));
        }

        return outputDestinations;
    }
}
