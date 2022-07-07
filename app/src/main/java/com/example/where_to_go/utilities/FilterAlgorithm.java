package com.example.where_to_go.utilities;

import androidx.annotation.NonNull;

import com.example.where_to_go.models.Destination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FilterAlgorithm {

    private final String INTENT;

    public FilterAlgorithm(String _intent) {
        INTENT = _intent;
    }

    @NonNull
    public static List<Destination> getTopRatedTour(@NonNull JSONArray businesses) throws JSONException {
        List<Destination> outputDestinations = new ArrayList<>();
        List<Destination> inputDestinations = new ArrayList<>();

        for (int pos = 0; pos < businesses.length(); ++pos) {
            Destination destination = new Destination();
            destination.setData(businesses.getJSONObject(pos));
            inputDestinations.add(destination);
        }

        inputDestinations.sort(new DestinationComparator());

        for (int pos = 0; pos < 10; ++pos) {
            outputDestinations.add(inputDestinations.get(pos));
        }

        return outputDestinations;
    }

    @NonNull
    public static List<Destination> getFilteredTour(JSONObject jsonFilteredResult, JSONArray businesses) throws JSONException {
        List<Destination> outputDestinations = new ArrayList<>();
        List<Destination> inputDestinations = new ArrayList<>();

        // TODO: Algorithms!

        for (int pos = 0; pos < businesses.length(); ++pos) {
            Destination destination = new Destination();
            destination.setData(businesses.getJSONObject(pos));
            inputDestinations.add(destination);
        }

        inputDestinations.sort(new DestinationComparator());

        for (int pos = 0; pos < 10; ++pos) {
            outputDestinations.add(inputDestinations.get(pos));
        }

        return outputDestinations;
    }
}
