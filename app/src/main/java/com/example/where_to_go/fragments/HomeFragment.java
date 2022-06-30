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
import com.example.where_to_go.adapters.ToursAdapter;
import com.example.where_to_go.models.Tours;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ToursAdapter featuredPathAdapter, recentPathAdapter;
    private List<Tours> featuredTours;
    private List<Tours> recentTours;

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

        // Featured Tours
        setFeaturedPathRecyclerView();
        getFeaturedPath();

        // Recent Tours
        setRecentPathRecyclerView();
        getRecentPath();
    }

    // HELPER METHODS

    private void setRecyclerView(RecyclerView recyclerView, ToursAdapter toursAdapter) {
        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(tLayoutManager);
        recyclerView.setHasFixedSize(true);
        // Set the Adapter on RecyclerView
        recyclerView.setAdapter(toursAdapter);
    }

    private void setFeaturedPathRecyclerView() {
        RecyclerView rvFeaturedPaths = requireView().findViewById(R.id.rvFeaturedTours);

        featuredTours = new ArrayList<>();
        featuredPathAdapter = new ToursAdapter(getContext(), featuredTours);

        setRecyclerView(rvFeaturedPaths, featuredPathAdapter);
    }

    private void setRecentPathRecyclerView() {
        RecyclerView rvRecentTours = requireView().findViewById(R.id.rvRecentTours);

        recentTours = new ArrayList<>();
        recentPathAdapter = new ToursAdapter(getContext(), recentTours);

        setRecyclerView(rvRecentTours, recentPathAdapter);
    }

    private void getFeaturedPath() {
        // Create a Query
        ParseQuery<Tours> destinationCollectionsParseQuery = ParseQuery.getQuery(Tours.class);

        // Include information we want to query
        destinationCollectionsParseQuery.include(Tours.USER_ID);

        // Query
        destinationCollectionsParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB", e);
                return;
            }
            featuredTours.addAll(_destinationCollections);
            featuredPathAdapter.notifyDataSetChanged();
        });
    }

    private void getRecentPath() {
        ParseQuery<Tours> destinationCollectionsParseQuery = ParseQuery.getQuery(Tours.class);
        final int LIMIT = 5;
        destinationCollectionsParseQuery.include(Tours.USER_ID)
                .addDescendingOrder(Tours.KEY_UPDATED_AT)
                .setLimit(LIMIT);

        destinationCollectionsParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB", e);
                return;
            }
            recentTours.addAll(_destinationCollections);
            recentPathAdapter.notifyDataSetChanged();
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