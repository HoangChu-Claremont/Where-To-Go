package com.example.where_to_go.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.where_to_go.R;
import com.example.where_to_go.adapters.PathTypesAdapter;
import com.example.where_to_go.adapters.TopPathsAdapter;
import com.example.where_to_go.models.Path;
import com.example.where_to_go.models.PathType;
import com.example.where_to_go.models.YelpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

//    private List<Path> topPaths;
    private PathTypesAdapter tAdapter;

    private List<PathType> pathTypes;
    private FrameLayout flPathType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toast.makeText(getContext(), "You're in Home!", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        TextView tvContinuePath = view.findViewById(R.id.tvContinuePath);
        CardView cvContinuePath = view.findViewById(R.id.cvContinuePath);

        // TODO: Recommendation Algorithm
        cvContinuePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Get Data
//        getTopPaths();

//        setUpRecyclerView();

        getPathTypes();
        setUpPathTypeRecyclerView();


    }

    private void getPathTypes() {
        pathTypes = new ArrayList<>();
        String PATH_TYPE_IMAGE_URL = "http://via.placeholder.com/300.png";
        pathTypes.add(new PathType("Top 10 Rated", PATH_TYPE_IMAGE_URL));
        pathTypes.add(new PathType("Top 10 Foodie", PATH_TYPE_IMAGE_URL));
    }

    private void setUpPathTypeRecyclerView() {
        RecyclerView rvTopPaths = getView().findViewById(R.id.rvTopPaths);

        tAdapter = new PathTypesAdapter(getContext(), pathTypes);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvTopPaths.setLayoutManager(tLayoutManager);
        rvTopPaths.setHasFixedSize(true); // always get top 10 paths

        // Set the Adapter on RecyclerView
        rvTopPaths.setAdapter(tAdapter);
        tAdapter.notifyDataSetChanged();
    }

    // HELPER METHODS

    private boolean hasPermission() {
        int network_permission_check = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int gps_permission_check = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return network_permission_check != PackageManager.PERMISSION_GRANTED && gps_permission_check != PackageManager.PERMISSION_GRANTED;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }
    };

//    private void getTopPaths() {
//
//        // query for top 10 paths based on average
//        topPaths = new ArrayList<>();
//        final YelpClient topPath = new YelpClient();
//
//        topPath.getResponse(-122.1483654685629, 37.484668049999996, 3, new Callback() {
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                try {
//                    String responseData = response.body().string();
//                    JSONObject jsonData = new JSONObject(responseData);
//                    JSONArray jsonResults = jsonData.getJSONArray("businesses");
//                    topPaths.addAll(Path.getTopRatedPath(jsonResults));
//
//                    // Avoid the "Only the original thread that created a view hierarchy
//                    // can touch its views adapter" error
//                    ((Activity) getContext()).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //change View Data
//                            tAdapter.notifyDataSetChanged();
//                        }
//                    });
//
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }

//    private void setUpRecyclerView() {
//        RecyclerView rvTopPaths = getView().findViewById(R.id.rvTopPaths);
//
//        // Create the Adapter
//        tAdapter = new TopPathsAdapter(getContext(), topPaths);
//
//        // Set Layout Manager
//        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        rvTopPaths.setLayoutManager(tLayoutManager);
//        rvTopPaths.setHasFixedSize(true); // always get top 10 paths
//
//        // Set the Adapter on RecyclerView
//        rvTopPaths.setAdapter(tAdapter);
//    }
}