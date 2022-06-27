package com.example.where_to_go.models;

public class FeaturedPath {

    private String featuredPathName;
    private String featuredPathImageUrl;

    public FeaturedPath(String typeName, String typeImageUrl) {
        featuredPathName = typeName;
        featuredPathImageUrl = typeImageUrl;
    }

    // GETTER
    public String getFeaturedPathName() {
        return featuredPathName;
    }

    public String getFeaturedPathImageUrl() {
        return featuredPathImageUrl;
    }
}
