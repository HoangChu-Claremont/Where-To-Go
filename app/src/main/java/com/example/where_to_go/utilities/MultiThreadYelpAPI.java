package com.example.where_to_go.utilities;

import android.util.Log;
import androidx.annotation.NonNull;
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

public class MultiThreadYelpAPI extends Thread {

    private static final String BUSINESS_SEARCH_URL = "https://api.yelp.com/v3/businesses/search";
    private static final String BUSINESS_DETAILS_URL = "https://api.yelp.com/v3/businesses";
    private static final int YELP_LIMIT_PER_REQUEST = 50;
    private static final String TAG = "MultithreadingYelpClient";

    private final String category;
    private JSONArray jsonResults;
    private final double currentLongitude;
    private final double currentLatitude;

    public MultiThreadYelpAPI(String _category, double _currentLongitude, double _currentLatitude) {
        category = _category;
        currentLongitude = _currentLongitude;
        currentLatitude = _currentLatitude;
    }

    @Override
    public void run() {
        try {
            query(currentLongitude, currentLatitude, category);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    // HELPER METHODS

    public JSONArray getJsonResults() {
        return jsonResults;
    }

    public void query(double currentLongitude, double currentLatitude, String category) throws JSONException, IOException {
        Log.i(TAG, "querying...");

        OkHttpClient client = new OkHttpClient.Builder().build();

        String url = buildUrlRequest(currentLongitude, currentLatitude, category);
        Log.i(TAG, "URL: " + url);

        // Make request
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + BuildConfig.YELP_API_KEY)
                .build();

        // Get response
        Call call = client.newCall(request);
        Response response = call.execute();

        // Test response body
        testResponse(response);
    }

    private void testResponse(@NonNull Response response) throws IOException, JSONException {
        Log.i(TAG, "testResponse");

        Log.i(TAG, "response code: " + response.code());
        String responseData = Objects.requireNonNull(response.body()).string();
        JSONObject jsonData = new JSONObject(responseData);
        jsonResults = jsonData.getJSONArray("businesses");
        Log.i(TAG, "jsonResults Size:" + jsonResults.length());
    }

    @NonNull
    private String buildUrlRequest(double currentLongitude, double currentLatitude, String category) {
        Log.i(TAG, "buildUrlRequest");

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(Objects.requireNonNull(HttpUrl.parse(BUSINESS_SEARCH_URL))).newBuilder();

        // Add URL Params
        urlBuilder.addQueryParameter("longitude", String.valueOf(currentLongitude));
        urlBuilder.addQueryParameter("latitude", String.valueOf(currentLatitude));
        urlBuilder.addQueryParameter("limit", String.valueOf(YELP_LIMIT_PER_REQUEST));
        urlBuilder.addQueryParameter("categories", category);

        return urlBuilder.build().toString();
    }
}
