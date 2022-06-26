package com.example.where_to_go.models;

import java.util.Comparator;

public class RatingComparator implements Comparator<Path> {

    @Override
    public int compare(Path path1, Path path2) {
        double rating1 = path1.getRating();
        double rating2 = path2.getRating();

        if (rating1 < rating2) { // Reverse order
            return 1;
        } else if (rating1 > rating2) {
            return -1;
        }
        return 0;
    }
}
