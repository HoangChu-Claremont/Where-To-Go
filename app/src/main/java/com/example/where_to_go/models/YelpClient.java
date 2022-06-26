package com.example.where_to_go.models;

import com.example.where_to_go.BuildConfig;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class YelpClient {
    private static final String TOP_PLACES_URL = "https://api.yelp.com/v3/businesses/search";

    public static final String API_KEY = "Vi7ucfNTeCNfQetr9DuZ69NBhIcqgp5hI3qMLOjZbo-M4YlGIfUtbnQ10_H-7uqj6UsHQTAtxnX49Zg6f3umGRT3WEl7A6b6iXn48Rhy4VqrfRl4v2LABAmxql22YnYx";

    public void getResponse(double currentLongitude, double currentLatitude, int limit, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(TOP_PLACES_URL).newBuilder();
        urlBuilder.addQueryParameter("longitude", String.valueOf(currentLongitude));
        urlBuilder.addQueryParameter("latitude", String.valueOf(currentLatitude));
        urlBuilder.addQueryParameter("limit", String.valueOf(limit));

        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + BuildConfig.API_KEY)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
