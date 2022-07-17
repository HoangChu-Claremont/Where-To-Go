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
import android.widget.ImageButton;
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
    private List<Tour> savedTours;
    private ToursAdapter toursAdapter;

    private ImageButton ibTourBookmark;
    private ImageButton ibRemove;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ibTourBookmark = view.findViewById(R.id.ibBookmark);
        ibRemove = view.findViewById(R.id.ibRemove);

        setUpUserLayout(view);
        setSavedTourRecyclerView();
        getSavedTours();
    }

    private void setUpUserLayout(@NonNull View view) {
        TextView tvAccountName = view.findViewById(R.id.account_name);
        TextView tvAccountTwitterName = view.findViewById(R.id.account_twitter_name);
        ParseUser currentUser = ParseUser.getCurrentUser();
        String accountName = currentUser.getUsername();
        String accountTwitterName = "@" + accountName;

        tvAccountName.setText(accountName);
        tvAccountTwitterName.setText(accountTwitterName);
    }

    // HELPER METHODS

    private void setSavedTourRecyclerView() {
        toursAdapter = new ToursAdapter(getContext(), savedTours);

        RecyclerView rvSavedTours = requireView().findViewById(R.id.rvSavedTours);

        savedTours = new ArrayList<>();
        toursAdapter = new ToursAdapter(getContext(), savedTours);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSavedTours.setLayoutManager(tLayoutManager);
        // Set the Adapter on RecyclerView
        rvSavedTours.setAdapter(toursAdapter);
    }

    private void getSavedTours() {
        ParseQuery<Tour> destinationCollectionsParseQuery = ParseQuery.getQuery(Tour.class);
        destinationCollectionsParseQuery.include(Tour.USER_ID)
                        .whereEqualTo(Tour.IS_SAVED, true);

        destinationCollectionsParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB", e);
                return;
            }
            savedTours.addAll(_destinationCollections);
            Log.i(TAG, String.valueOf(savedTours.size()));
            toursAdapter.notifyDataSetChanged();
        });
    }
}