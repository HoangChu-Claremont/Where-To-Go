package com.example.where_to_go.models;

public class DestinationCollections {

    private String collectionName;
    private String collectionImageUrl;

    public DestinationCollections(String _collectionName, String _collectionImageUrl) {
        collectionName = _collectionName;
        collectionImageUrl = _collectionImageUrl;
    }

    // GETTER
    public String getPathName() {
        return collectionName;
    }

    public String getPathImageUrl() {
        return collectionImageUrl;
    }
}
