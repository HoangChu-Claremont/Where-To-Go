package com.example.where_to_go.utilities;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.where_to_go.models.Destination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class FilterAlgorithm {

    private static final String TAG = "FilterAlgorithm";
    private static final int AVG_SLEEP_HOURS_PER_DAY = 8;
    private static final int MAX_PERCENTAGE = 100;
    private static final int TIME_PER_DESTINATION = 2; // TODO: Calculate for each category
    private static final double INT_TO_FLOAT = 1.0;
    private static final int HOURS_PER_DAY = 24;
    private static int numberOfTours; // TODO: Need a better way to calculate this as well

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
    public static List<Destination> getFilteredTour(@NonNull JSONObject jsonFilteredResult,
                                                    HashMap<String, JSONArray> _categoryDestinationsMap) throws JSONException {
        List<Destination> outputDestinations;
        List<String> categories = Arrays.asList(jsonFilteredResult.getString("destination_type").split(","));

        Log.i(TAG, "jsonFilteredResult: " + jsonFilteredResult);
        Log.i(TAG, "_categoryDestinationsMap: " + _categoryDestinationsMap);

        // Preliminary work
        List<Pair<String, Integer>> sortedPreferenceMap = getPreferenceMap(jsonFilteredResult, categories); // Descending order
        sortedPreferenceMap.sort((o1, o2) -> {
            if (o1.second < o2.second) { // Descending order
                return 1;
            } else if (o1.second > o2.second) {
                return -1;
            }
            return 0;
        });

        HashMap<String, Integer> noDestinationsPerCategory = getNoDestinationsPerCategory(jsonFilteredResult.getInt("no_days"), sortedPreferenceMap);

        // Step 1
        List<String> orderedCategories = getOrderedCategories(noDestinationsPerCategory, categories);
        Log.i(TAG, "orderedCategories: " + orderedCategories);
        // Step 2
        List<Pair<List<Destination>, Double>> builtRatedTours = buildTours(orderedCategories, _categoryDestinationsMap);
        Log.i(TAG, "builtRatedTours size: " + builtRatedTours.size());
        // Step 3
        outputDestinations = getBestRatedTour(builtRatedTours);

        return outputDestinations;
    }


    // HELPER METHODS

    private static List<Destination> getBestRatedTour(@NonNull List<Pair<List<Destination>, Double>> _builtRatedTours) {
        double bestRating = 0.0;
        List<Destination> bestTour = new ArrayList<>();

        for (Pair<List<Destination>, Double> builtRatedTour : _builtRatedTours) {
            if (builtRatedTour.second > bestRating) {
                bestRating = builtRatedTour.second;
                bestTour = builtRatedTour.first;
            }
        }

        return bestTour;
    }

    @NonNull
    private static List<Pair<List<Destination>, Double>> buildTours(List<String> _orderedCategories,
                                                                    HashMap<String, JSONArray> _categoryDestinationsMap) throws JSONException {

        Log.i(TAG, "_categoryDestinationsMap: " + _categoryDestinationsMap);

        List<Pair<List<Destination>, Double>> returningTours = new ArrayList<>();
        HashSet<String> seenDestinations = new HashSet<>();

        for (int i = 0; i < numberOfTours; ++i) {
            // Get a starting destination
            Log.i(TAG, "You're starting destination: " + i);

            // Get an ordered array of categories
            List<Destination> builtTour = new ArrayList<>();
            String currentCategory = _orderedCategories.get(i);
            JSONArray jsonDestinations = _categoryDestinationsMap.get(currentCategory);

            // Select a starting position
            Destination startingDestination = getStartingDestination(jsonDestinations, seenDestinations);
            builtTour.add(startingDestination);
            seenDestinations.add(startingDestination.getYelpId());

            // Build a tour recursively
            Pair<List<Destination>, Double> aTourWithRatings = buildOneTour(builtTour, _orderedCategories,
                    1, startingDestination.getRating(), _categoryDestinationsMap, seenDestinations);

            // Update and Reset for a new tour
            returningTours.add(aTourWithRatings);
            seenDestinations = new HashSet<>();
            seenDestinations.add(startingDestination.getYelpId());
        }

        return returningTours;
    }

    @NonNull
    private static Destination getStartingDestination(JSONArray jsonDestinations, HashSet<String> seenDestinations) throws JSONException {
        return getBestRatedDestination(jsonDestinations, seenDestinations);
    }

    @NonNull
    private static Destination getBestRatedDestination(@NonNull JSONArray jsonDestinations, HashSet<String> seenDestinations) throws JSONException {
        double bestRating = 0.0;
        Destination bestRatedDestination = new Destination();

        Log.i(TAG, "jsonDestinations: " + jsonDestinations);
        Log.i(TAG, "seenDestinations: " + seenDestinations);

        for (int i = 0; i < jsonDestinations.length(); ++i) {
            JSONObject jsonCurrentDestination = jsonDestinations.getJSONObject(i);
            double currentRating = jsonCurrentDestination.getDouble("rating");
            int totalReview = jsonCurrentDestination.getInt("review_count");
            double totalRating = currentRating * totalReview;

            if (totalRating > bestRating && !seenDestinations.contains(jsonCurrentDestination.getString("id"))) {
                bestRating = totalRating;
                bestRatedDestination.setData(jsonCurrentDestination);
            }
        }

        return bestRatedDestination;
    }

    @NonNull
    private static Pair<List<Destination>, Double> buildOneTour(List<Destination> _builtTour, @NonNull List<String> _orderedCategories,
                                                                int _currentCategoryOrder, double _totalRating,
                                                                HashMap<String, JSONArray> _categoryDestinationsMap, HashSet<String> seenDestinations)
            throws JSONException {

        Log.i(TAG, "Currently pick destination #:" + _currentCategoryOrder);
        // Base case
        if (_currentCategoryOrder == _orderedCategories.size()) {
            return new Pair<>(_builtTour, _totalRating / _builtTour.size());
        }

        // Get all destinations of a category
        String currentCategory = _orderedCategories.get(_currentCategoryOrder);
        JSONArray jsonDestinations = _categoryDestinationsMap.get(currentCategory);

        assert jsonDestinations != null;

        // Find the next closest destination
        Destination nextClosestDestination = getNextClosestDestination(jsonDestinations, seenDestinations);

        // Add a destination and update total rating
        _builtTour.add(nextClosestDestination);
        seenDestinations.add(nextClosestDestination.getYelpId());
        _totalRating += nextClosestDestination.getRating();

        return buildOneTour(_builtTour, _orderedCategories, _currentCategoryOrder + 1, _totalRating, _categoryDestinationsMap, seenDestinations);
    }

    @NonNull
    private static Destination getNextClosestDestination(@NonNull JSONArray jsonDestinations, HashSet<String> seenDestinations) throws JSONException {
        double closestDistance = Double.POSITIVE_INFINITY;
        Destination closestDestination = new Destination();

        for (int i = 0; i < jsonDestinations.length(); ++i) {
            JSONObject jsonCurrentDestination = jsonDestinations.getJSONObject(i);
            Destination targetDestination = new Destination();
            targetDestination.setData(jsonCurrentDestination);

            double currentDestinationDistance = closestDestination.setCustomDistance(targetDestination.getLongitude(), targetDestination.getLatitude());

            if (currentDestinationDistance < closestDistance && !seenDestinations.contains(jsonCurrentDestination.getString("id"))) {
                closestDistance = currentDestinationDistance;
                closestDestination.setData(jsonCurrentDestination);
            }
        }

        return closestDestination;
    }

    @NonNull
    private static List<String> getOrderedCategories(HashMap<String, Integer> _noDestinationsPerCategory, @NonNull List<String> _categories) {
        // TODO: Make this not random

        List<String> returningOrderedCategories = new ArrayList<>();
        Random rand = new Random();

        // Get bound and size of a tour
        int bound = _categories.size();
        int noDestinations = 0;
        for (String category : _categories) {
            assert _noDestinationsPerCategory.containsKey(category);
            noDestinations += _noDestinationsPerCategory.get(category);
        }

        for (int i = 0; i < noDestinations; ++i) {
            int nextRandomNum = rand.nextInt(bound);
            String randomCategory = _categories.get(nextRandomNum);
            returningOrderedCategories.add(randomCategory);
        }

        return returningOrderedCategories;
    }

    @NonNull
    private static List<Pair<String, Integer>> getPreferenceMap(@NonNull JSONObject _jsonFilteredResult, @NonNull List<String> _categories) throws JSONException {
        List<Pair<String, Integer>> returnPreferenceMap = new ArrayList<>();

        List jsonPreferenceArray = (List) _jsonFilteredResult.get("preference_values"); // get() returns Object, but we know it's a list

        for (int i = 0; i < _categories.size(); ++i) { // size of 'categories' == size of jsonPreferenceArray
            String category = _categories.get(i);
            int preference = (int) jsonPreferenceArray.get(i);
            returnPreferenceMap.add(new Pair<>(category, preference));
        }

        return returnPreferenceMap;
    }

    @NonNull
    private static HashMap<String, Integer> getNoDestinationsPerCategory(int _noDays, @NonNull List<Pair<String, Integer>> _sortedPreferenceMap) {
        HashMap<String, Integer> resultNoDestinationsPerCategory = new HashMap<>();
        int maxNoTours = 0;

        // N days -> N-1 sleeping nights
        int totalActivityHours = HOURS_PER_DAY * _noDays - AVG_SLEEP_HOURS_PER_DAY * (_noDays - 1) ; // TODO: Need to account driving time as well

        for (Pair<String, Integer> categoryPreference : _sortedPreferenceMap) {
            String category = categoryPreference.first;
            int preference = categoryPreference.second;

            // Compute
            double maxHoursThisCategory = (INT_TO_FLOAT * totalActivityHours) * preference / MAX_PERCENTAGE;
            int totalDestinationsThisCategory = (int) Math.floor(maxHoursThisCategory / TIME_PER_DESTINATION); // Floor down, so users will always have sufficient time as planned

            // Update
            resultNoDestinationsPerCategory.put(category, totalDestinationsThisCategory);
            maxNoTours = Math.max(maxNoTours, totalDestinationsThisCategory);
        }

        numberOfTours = maxNoTours;

        return resultNoDestinationsPerCategory;
    }
}
