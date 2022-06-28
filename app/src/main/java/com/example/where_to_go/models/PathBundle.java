package com.example.where_to_go.models;

public class PathBundle {

    private String bundlePathName;
    private String bundlePathImageUrl;

    public PathBundle(String _bundlePathName, String _bundlePathImageUrl) {
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
