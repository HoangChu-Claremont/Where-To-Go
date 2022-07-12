package com.example.where_to_go.utilities;

import android.util.Log;

import com.example.where_to_go.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MultiThread extends Thread {

    private static final String BUSINESS_SEARCH_URL = "https://api.yelp.com/v3/businesses/search";
    private static final String BUSINESS_DETAILS_URL = "https://api.yelp.com/v3/businesses";
    private static final int YELP_LIMIT_PER_REQUEST = 50;
    private static final String TAG = "YelpClient";
    private String category;

    JSONArray jsonResults;

    private double currentLongitude;
    private double currentLatitude;

    public MultiThread(String _category, double _currentLongitude, double _currentLatitude) {
        category = _category;
        currentLongitude = _currentLongitude;
        currentLatitude = _currentLatitude;
    }

    public JSONArray getCategoryDestinationsMap() {
        return jsonResults;
    }

    @Override
    public void run() {
        try {
            query(currentLongitude, currentLatitude, category);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public void query(double currentLongitude, double currentLatitude, String category) throws JSONException, IOException {

        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(Objects.requireNonNull(HttpUrl.parse(BUSINESS_SEARCH_URL))).newBuilder();

        urlBuilder.addQueryParameter("longitude", String.valueOf(currentLongitude));
        urlBuilder.addQueryParameter("latitude", String.valueOf(currentLatitude));
        urlBuilder.addQueryParameter("limit", String.valueOf(YELP_LIMIT_PER_REQUEST));
        urlBuilder.addQueryParameter("categories", category);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + BuildConfig.YELP_API_KEY)
                .build();

        Call call = client.newCall(request);

        Response response = call.execute();
        assert response.code() == 200;
        Log.i(TAG, "response code: " + response.code());
        String responseData = Objects.requireNonNull(response.body()).string();
        JSONObject jsonData = new JSONObject(responseData);
        jsonResults = jsonData.getJSONArray("businesses");
    }
}
