package com.example.where_to_go.utilities;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.where_to_go.models.Destination;
import java.util.Comparator;

public class DestinationComparator implements Comparator<Destination> {

    private static final String TAG = "DestinationComparator";

    @Override
    public int compare(@NonNull Destination destination1, @NonNull Destination destination2) {
        Log.i(TAG, "comparing 2 destinations in descending order");
        double rating1 = destination1.getRating();
        double rating2 = destination2.getRating();

        if (rating1 < rating2) { // Descending Order
            return 1;
        } else if (rating1 > rating2) {
            return -1;
        }
        return 0;
    }
}
