package com.example.where_to_go.utilities;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.models.Tour;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseUtils {

    private static final String TAG = "DatabaseUtils";

    // TourDB
    @NonNull
    public static List<Tour> getFeaturedToursFromDatabase() {
        Log.i(TAG, "getFeaturedToursFromDatabase");

        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        List<Tour> outputTours = new ArrayList<>();

        tourParseQuery.clearCachedResult();
        tourParseQuery.whereEqualTo(Tour.IS_FEATURED, true);

        try {
            outputTours = tourParseQuery.find();
        } catch (ParseException e) {
            Log.e(TAG, "Issues with getting featured tours from DB: " + e.getMessage());
        }

        Log.i(TAG, "featuredTours size: " + outputTours.size());
        return outputTours;
    }

    @NonNull
    public static List<Tour> getLimitedRecentToursFromDatabase(int limit) {
        Log.i(TAG, "getLimitedRecentToursFromDatabase");

        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        List<Tour> outputTours = new ArrayList<>();

        tourParseQuery.clearCachedResult();

        tourParseQuery.addDescendingOrder(Tour.KEY_UPDATED_AT)
                .setLimit(limit);

        try {
            outputTours = tourParseQuery.find();
        } catch (ParseException e) {
            Log.e(TAG, "Issues with getting recent tours from DB: " + e.getMessage());
        }

        Log.i(TAG, "recentTours size: " + outputTours.size());
        return outputTours;
    }

    @NonNull
    public static List<Tour> getAllToursFromDatabase() {
        Log.i(TAG, "getAllToursFromDatabase");

        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        List<Tour> outputTours = new ArrayList<>();

        try {
            outputTours = tourParseQuery.find();
        } catch (ParseException e) {
            Log.i(TAG, "Can't get tours from DB: " + e.getMessage());
        }

        Log.i(TAG, "allTours size: " + outputTours.size());
        return outputTours;
    }

    @NonNull
    public static List<String> getTourIdFromDatabaseByMostRecentlyUpdated() {
        Log.i(TAG, "getTourIdFromDatabaseByMostRecentlyUpdated");

        List<String> tourIDs = new ArrayList<>();

        
        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        tourParseQuery.addDescendingOrder("updatedAt")
                .selectKeys(Collections.singletonList(Tour.KEY_OBJECT_ID));

        try {
            List<Tour> tourFounds = tourParseQuery.find();
            for (Tour tourFound : tourFounds) {
                tourIDs.add(tourFound.getObjectId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "tourIDs: " + tourIDs);
        return tourIDs;
    }

    public static void removeOneTourFromDatabaseIfExists(String tourIdToRemove) {
        Log.i(TAG, "removeOneTourFromDatabaseIfExists");
        Log.i(TAG, "tourIdToRemove: " + tourIdToRemove);

        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        ParseQuery<Destination> destinationParseQuery = ParseQuery.getQuery(Destination.class);

        if (hasTourExisted(tourIdToRemove)) {
            ParseObject tourDB_Object = ParseObject.createWithoutData(Tour.class, tourIdToRemove);

            // Remove the tour from DB
            tourParseQuery.getInBackground(tourIdToRemove, (tourToRemove, e) -> removeTourFromDB(tourToRemove));

            // Remove destinations associated with removed tour
            destinationParseQuery.whereEqualTo(Destination.TOUR_ID, tourDB_Object);
            destinationParseQuery.findInBackground((destinationsToRemove, e) -> {
                for (Destination destinationToRemove : destinationsToRemove) {
                    removeDestinationFromDB(destinationToRemove);
                }
            });
        }
    }

    public static String saveOneTourToDatabaseAndReturnID(String tourName, @NonNull ParseUser currentUser, String googleMapsURL) {
        Log.i(TAG, "saveOneTourToDatabaseAndReturnID");

        Tour tour = new Tour();
        String returnedTourId = "";

        // Getting information to set up the POST query
        tour.put(Tour.USER_ID, ParseObject.createWithoutData(ParseUser.class, currentUser.getObjectId()));
        tour.setTourNameDB(tourName);
        tour.setTransportationSecondsDB(0); // TODO: Create an algorithm to calculate this
        tour.setGoogleMapsURL(googleMapsURL);

        try {
            tour.save();
            returnedTourId = tour.getObjectId();
        } catch (ParseException e) {
            Log.i(TAG, "Can't save tour. " + e.getMessage());
        }

        Log.i(TAG, "returnedTourId: " + returnedTourId);
        return returnedTourId;
    }

    public static String getGoogleMapsURLFromOneTour(String tourName, @NonNull ParseUser currentUser) {
        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        String outputURL = "";

        tourParseQuery.include("googleMapsURL").include(currentUser.getObjectId())
                .whereEqualTo("tour_name", tourName);

        try {
            outputURL = tourParseQuery.find().get(0).getGoogleMapsUrlDB();
        } catch (ParseException e) {
            Log.i(TAG, "Can't get GoogleMapsURL from DB. " + e.getMessage());
            e.printStackTrace();
        }

        Log.i(TAG, "outputURL: " + outputURL);
        return outputURL;
    }

    // DestinationDB
    @NonNull
    public static List<Destination> getAllUnPackedDestinationsFromATour(String tourId) {
        Log.i(TAG, "getAllUnPackedDestinationsFromATour");

        ParseQuery<Destination> destinationParseQuery = ParseQuery.getQuery(Destination.class);
        List<Destination> outputDestinations = new ArrayList<>();

        if (hasTourExisted(tourId)) {
            ParseObject tourDB_Object = ParseObject.createWithoutData(Tour.class, tourId);

            // Remove destinations associated with removed tour
            destinationParseQuery.whereEqualTo(Destination.TOUR_ID, tourDB_Object);

            try {
                outputDestinations = destinationParseQuery.find()
                        .stream().distinct().collect(Collectors.toList()); // Remove duplicates
            } catch (ParseException e) {
                Log.e(TAG, "Issues with getting destinations from DB: " + e.getMessage());
            }
        }

        Log.i(TAG, "outputDestinations size: " + outputDestinations.size());
        return outputDestinations;
    }

    public static void removeDestinationsFromDatabaseIfExists(String destinationIdToRemove) {
        Log.i(TAG, "removeDestinationsFromDatabaseIfExists");
        Log.i(TAG, "destinationIdToRemove: " + destinationIdToRemove);

        ParseQuery<Destination> destinationParseQuery = ParseQuery.getQuery(Destination.class);

        if (hasDestinationExisted(destinationIdToRemove)) {
            destinationParseQuery.getInBackground(destinationIdToRemove, (destinationToRemove, e)
                    -> removeDestinationFromDB(destinationToRemove));
        }
    }

    public static boolean saveDestinationsToDatabase(@NonNull List<Destination> destinations, @NonNull String tourId) {
        Log.i(TAG, "saveDestinationsToDatabase");
        Log.i(TAG, "saveToDestinationsDB: " + tourId);

        for (Destination destination : destinations) {
            // Getting information to set up the POST query
            destination.put("tour_id", ParseObject.createWithoutData(Tour.class, tourId));
            destination.putToDB();

            try {
                destination.save();
            } catch (ParseException e) {
                Log.i(TAG, "Error while saving this destination: " + e.getMessage());
                return false;
            }
        }

        return true;
    }

    // HELPER METHODS

    private static boolean hasDestinationExisted(String destinationIdToRemove) {
        Log.i(TAG, "hasDestinationExisted");

        ParseQuery<Destination> destinationParseQuery = ParseQuery.getQuery(Destination.class);
        boolean hasExisted = false;

        destinationParseQuery.whereEqualTo(Destination.KEY_OBJECT_ID, destinationIdToRemove);

        try {
        Destination returnDestination = destinationParseQuery.getFirst();
            hasExisted = true;
            Log.i(TAG, returnDestination.getLocationNameDB());
        } catch (Exception e) {
            Log.i(TAG, "Can't find destination. " + e.getMessage());
        }

        Log.i(TAG, "hasDestinationExisted: " + hasExisted);
        return hasExisted;
    }

    private static boolean hasTourExisted(String tourIdToRemove) {
        Log.i(TAG, "hasTourExisted");

        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        boolean hasExisted = false;

        tourParseQuery.whereEqualTo(Tour.KEY_OBJECT_ID, tourIdToRemove);

        try {
            Tour returnTour = tourParseQuery.getFirst();
            hasExisted = true;
            Log.i(TAG, returnTour.getTourNameDB());
        } catch (Exception e) {
            Log.i(TAG, "Can't find tour. " + e.getMessage());
        }

        Log.i(TAG, "hasTourExisted: " + hasExisted);
        return hasExisted;
    }

    private static void removeTourFromDB(@NonNull Tour tourToRemove) {
        Log.i(TAG, "removeTourFromDB");
        Log.i(TAG, "tourToRemove: " + tourToRemove);

        try {
            tourToRemove.delete();
            tourToRemove.saveInBackground();
        } catch (ParseException e) {
            Log.i(TAG, "Can't delete tour. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void removeDestinationFromDB(@NonNull Destination destinationToRemove) {
        Log.i(TAG, "removeDestinationFromDB");
        Log.i(TAG, "destinationToRemove: " + destinationToRemove);

        try {
            destinationToRemove.delete();
            destinationToRemove.saveInBackground();
        } catch (ParseException e) {
            Log.i(TAG, "Can't delete destination. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
