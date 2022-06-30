package com.example.where_to_go.fragments;

import android.Manifest;
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
import android.widget.Toast;

import com.example.where_to_go.R;
import com.example.where_to_go.adapters.FeaturedPathAdapter;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.models.DestinationCollections;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FeaturedPathAdapter featuredPathAdapter;
    private List<DestinationCollections> destinationCollections;

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

        CardView cvContinuePath = view.findViewById(R.id.cvContinuePath);

        // TODO: Recommendation Algorithm
        cvContinuePath.setOnClickListener(v -> {

        });

        // Featured Destination
        setFeaturedPathRecyclerView();
        getFeaturedPath();

    }

    // HELPER METHODS

//    private void getFeaturedPath() {
//        String PATH_TYPE_IMAGE_URL = "http://via.placeholder.com/300.png";
//        destinationCollections = new ArrayList<>();
//
//        destinationCollections.add(new DestinationCollections("Top 10 Rated"));
//        destinationCollections.add(new DestinationCollections("Top 10 Foodie"));
//    }

    private void setFeaturedPathRecyclerView() {
        RecyclerView rvFeaturedPaths = requireView().findViewById(R.id.rvFeaturedTours);

        destinationCollections = new ArrayList<>();
        featuredPathAdapter = new FeaturedPathAdapter(getContext(), destinationCollections);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvFeaturedPaths.setLayoutManager(tLayoutManager);
        rvFeaturedPaths.setHasFixedSize(true); // always get top 10 paths
        // Set the Adapter on RecyclerView
        rvFeaturedPaths.setAdapter(featuredPathAdapter);
    }

    private void getFeaturedPath() {
        ParseQuery<DestinationCollections> destinationCollectionsParseQuery = ParseQuery.getQuery(DestinationCollections.class);

        destinationCollectionsParseQuery.include(DestinationCollections.USER_ID);

        destinationCollectionsParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB", e);
                return;
            }
            destinationCollections.addAll(_destinationCollections);
            featuredPathAdapter.notifyDataSetChanged();
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }
    };

    private boolean hasPermission() {
        int network_permission_check = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int gps_permission_check = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return network_permission_check != PackageManager.PERMISSION_GRANTED && gps_permission_check != PackageManager.PERMISSION_GRANTED;
    }
}