package com.example.where_to_go.utilities;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.where_to_go.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YelpClient {
    private static final String BUSINESS_SEARCH_URL = "https://api.yelp.com/v3/businesses/search";
    private static final String BUSINESS_DETAILS_URL = "https://api.yelp.com/v3/businesses";
    private static final int YELP_LIMIT_PER_REQUEST = 50;
    private static final String TAG = "YelpClient";

//    public HashMap<String, JSONArray> query(double currentLongitude, double currentLatitude, String category, HashMap<String, JSONArray> categoryDestinationsMap) throws JSONException {
//
//        OkHttpClient client = new OkHttpClient.Builder().build();
//        HttpUrl.Builder urlBuilder = Objects.requireNonNull(Objects.requireNonNull(HttpUrl.parse(BUSINESS_SEARCH_URL))).newBuilder();
//
//        urlBuilder.addQueryParameter("longitude", String.valueOf(currentLongitude));
//        urlBuilder.addQueryParameter("latitude", String.valueOf(currentLatitude));
//        urlBuilder.addQueryParameter("limit", String.valueOf(YELP_LIMIT_PER_REQUEST));
//        urlBuilder.addQueryParameter("categories", category);
//        String url = urlBuilder.build().toString();
//
//        Log.i(TAG, url);
//
//        Request request = new Request.Builder()
//                .url(url)
//                .header("Authorization", "Bearer " + BuildConfig.YELP_API_KEY)
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                String responseData = Objects.requireNonNull(response.body()).string();
//                JSONObject jsonData = new JSONObject(responseData);
//                JSONArray jsonResults = jsonData.getJSONArray("businesses");
//                categoryDestinationsMap.put(category, jsonResults);
//            }
//        });
//    }
}
