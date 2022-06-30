package com.example.where_to_go.utilities;

import com.example.where_to_go.models.Destination;

import java.util.Comparator;

public class RatingComparator implements Comparator<Destination> {

    @Override
    public int compare(Destination destination1, Destination destination2) {
        double rating1 = destination1.getRating();
        double rating2 = destination2.getRating();

        if (rating1 < rating2) { // Reverse order
            return 1;
        } else if (rating1 > rating2) {
            return -1;
        }
        return 0;
    }
}
