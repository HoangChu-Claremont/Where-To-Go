package com.example.where_to_go.utilities;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.models.Tour;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    private static final String TAG = "DatabaseUtils";

    // TourDB
    public static void removeOneTourFromDatabaseIfExists(String tourIdToRemove) {
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

    @NonNull
    public static List<Tour> getFeaturedToursFromDatabase() {
        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        List<Tour> outputTours = new ArrayList<>();

        tourParseQuery.whereEqualTo(Tour.IS_FEATURED, true);

        tourParseQuery.findInBackground((returnedTours, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting featured tours from DB", e);
            } else {
                outputTours.addAll(returnedTours);
            }
        });

        Log.i(TAG, "featuredTours size: " + outputTours.size());
        return outputTours;
    }

    @NonNull
    public static List<Tour> getLimitedRecentToursFromDatabase(int limit) {
        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        List<Tour> outputTours = new ArrayList<>();

        tourParseQuery.addDescendingOrder(Tour.KEY_UPDATED_AT)
                .setLimit(limit);

        tourParseQuery.findInBackground((returnedTours, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting recent tours from DB", e);
            } else {
                outputTours.addAll(returnedTours);
            }
        });

        Log.i(TAG, "recentTours size: " + outputTours.size());
        return outputTours;
    }

    // DestinationDB
    public static void removeDestinationsFromDatabaseIfExists(String destinationIdToRemove) {
        Log.i(TAG, "destinationIdToRemove: " + destinationIdToRemove);

        ParseQuery<Destination> destinationParseQuery = ParseQuery.getQuery(Destination.class);

        if (hasDestinationExisted(destinationIdToRemove)) {
            destinationParseQuery.getInBackground(destinationIdToRemove, (destinationToRemove, e)
                    -> removeDestinationFromDB(destinationToRemove));
        }
    }

    // HELPER METHODS

    private static boolean hasDestinationExisted(String destinationIdToRemove) {
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
