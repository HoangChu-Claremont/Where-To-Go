package com.example.where_to_go.models;

public class DestinationCollections {

    private String bundlePathName;
    private String bundlePathImageUrl;

    public DestinationCollections(String _bundlePathName, String _bundlePathImageUrl) {
        bundlePathName = _bundlePathName;
        bundlePathImageUrl = _bundlePathImageUrl;
    }

    // GETTER
    public String getPathName() {
        return bundlePathName;
    }

    public String getPathImageUrl() {
        return bundlePathImageUrl;
    }
}
