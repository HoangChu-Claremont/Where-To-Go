package com.example.where_to_go.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
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
import com.example.where_to_go.models.Tour;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    // TODO: Work on moving between fragments
    private static final String TAG = "HomeFragment";

    private ToursAdapter featuredTourAdapter, recentTourAdapter;
    private List<Tour> featuredTours;
    private List<Tour> recentTours;

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

        CardView cvContinueTour = view.findViewById(R.id.cvContinueTour);

        // TODO: Recommendation Algorithm
        cvContinueTour.setOnClickListener(v -> {

        });

        // Setting up RecyclerView
        setFeaturedToursRecyclerView();
        setRecentToursRecyclerView();

        // Get Tour
        getFeaturedTours();
        getRecentTours();
    }

    // HELPER METHODS

    private void setRecyclerView(@NonNull RecyclerView recyclerView, ToursAdapter toursAdapter) {
        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(tLayoutManager);
        recyclerView.setHasFixedSize(true);
        // Set the Adapter on RecyclerView
        recyclerView.setAdapter(toursAdapter);
    }

    private void setFeaturedToursRecyclerView() {
        RecyclerView rvFeaturedTours = requireView().findViewById(R.id.rvFeaturedTours);

        featuredTours = new ArrayList<>();
        featuredTourAdapter = new ToursAdapter(getContext(), featuredTours);

        setRecyclerView(rvFeaturedTours, featuredTourAdapter);
    }

    private void setRecentToursRecyclerView() {
        RecyclerView rvRecentTours = requireView().findViewById(R.id.rvRecentTours);

        recentTours = new ArrayList<>();
        recentTourAdapter = new ToursAdapter(getContext(), recentTours);

        setRecyclerView(rvRecentTours, recentTourAdapter);
    }

    private void getFeaturedTours() {
        // Create a Query
        ParseQuery<Tour> destinationCollectionsParseQuery = ParseQuery.getQuery(Tour.class);

        // Include information we want to query
        destinationCollectionsParseQuery.include(Tour.USER_ID);

        // Query
        destinationCollectionsParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB", e);
                return;
            }
            featuredTours.addAll(_destinationCollections);
            featuredTourAdapter.notifyDataSetChanged();
        });
    }

    private void getRecentTours() {
        ParseQuery<Tour> destinationCollectionsParseQuery = ParseQuery.getQuery(Tour.class);
        final int LIMIT = 5;
        destinationCollectionsParseQuery.include(Tour.USER_ID)
                .addDescendingOrder(Tour.KEY_UPDATED_AT)
                .setLimit(LIMIT);

        destinationCollectionsParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB", e);
                return;
            }
            recentTours.addAll(_destinationCollections);
            recentTourAdapter.notifyDataSetChanged();
        });
    }

    private final LocationListener mLocationListener = location -> {
        //your code here
    };

    private boolean hasPermission() {
        int network_permission_check = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int gps_permission_check = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return network_permission_check != PackageManager.PERMISSION_GRANTED && gps_permission_check != PackageManager.PERMISSION_GRANTED;
    }
}