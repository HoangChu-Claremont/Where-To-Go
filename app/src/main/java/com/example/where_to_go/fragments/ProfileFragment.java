package com.example.where_to_go.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.where_to_go.R;
import com.example.where_to_go.adapters.ToursAdapter;
import com.example.where_to_go.models.Tour;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView tvAccountName;
    private TextView tvAccountTwitterName;
    private List<Tour> savedTours;
    private ToursAdapter profileAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(), "You're in Profile!", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAccountName = view.findViewById(R.id.account_name);
        tvAccountTwitterName = view.findViewById(R.id.account_twitter_name);

        ParseUser currentUser = ParseUser.getCurrentUser();

        String accountName = currentUser.getUsername();
        String accountTwitterName = "@" + accountName;

        tvAccountName.setText(accountName);
        tvAccountTwitterName.setText(accountTwitterName);

        setSavedTourRecyclerView();
        getSavedTour();
    }

    // HELPER METHODS

    private void setSavedTourRecyclerView() {
        profileAdapter = new ToursAdapter(getContext(), savedTours);

        RecyclerView rvSavedTours = requireView().findViewById(R.id.rvSavedTours);

        savedTours = new ArrayList<>();
        profileAdapter = new ToursAdapter(getContext(), savedTours);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSavedTours.setLayoutManager(tLayoutManager);
        rvSavedTours.setHasFixedSize(true);
        // Set the Adapter on RecyclerView
        rvSavedTours.setAdapter(profileAdapter);
    }

    private void getSavedTour() {
        ParseQuery<Tour> destinationCollectionsParseQuery = ParseQuery.getQuery(Tour.class);
        destinationCollectionsParseQuery.include(Tour.USER_ID)
                        .whereEqualTo("isSaved", true);

        destinationCollectionsParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB", e);
                return;
            }
            savedTours.addAll(_destinationCollections);
            Log.i(TAG, String.valueOf(savedTours.size()));
            profileAdapter.notifyDataSetChanged();
        });
    }
}