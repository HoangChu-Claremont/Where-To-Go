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
import com.example.where_to_go.R;
import com.example.where_to_go.activities.LoginActivity;
import com.example.where_to_go.adapters.FriendsAdapter;
import com.example.where_to_go.adapters.ToursAdapter;
import com.example.where_to_go.models.Friend;
import com.example.where_to_go.models.Tour;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private List<Tour> savedTours;
    private ToursAdapter toursAdapter;

    private List<Friend> friends;
    private FriendsAdapter friendsAdapter;


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

        setUpUserLayout(view);

        setSavedFriendsRecyclerView();
        setSavedTourRecyclerView();

        getFriends();
        getSavedTours();
    }

    private void getFriends() {
        Log.i(TAG, "getFriends");

        friends = LoginActivity.friends;
        friendsAdapter.notifyDataSetChanged();
    }

    private void setSavedFriendsRecyclerView() {
        Log.i(TAG, "setSavedFriendsRecyclerView");

        RecyclerView rvFriends = requireView().findViewById(R.id.rvFriends);

        friends = LoginActivity.friends;
        friendsAdapter = new FriendsAdapter(getContext(), friends);

        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvFriends.setLayoutManager(tLayoutManager);

        rvFriends.setAdapter(friendsAdapter);
    }

    private void setUpUserLayout(@NonNull View view) {
        Log.i(TAG, "setUpUserLayout");

        TextView tvAccountName = view.findViewById(R.id.account_name);
        TextView tvAccountTwitterName = view.findViewById(R.id.account_twitter_name);

        String accountName = (ParseUser.getCurrentUser().getUsername() != null) ?
                ParseUser.getCurrentUser().getUsername() : LoginActivity.username;
        String accountTwitterName = "@" + accountName;

        tvAccountName.setText(accountName);
        tvAccountTwitterName.setText(accountTwitterName);
    }

    // HELPER METHODS

    private void setSavedTourRecyclerView() {
        Log.i(TAG, "setSavedTourRecyclerView");

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
        Log.i(TAG, "getSavedTours");

        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        tourParseQuery.include(Tour.USER_ID)
                        .whereEqualTo(Tour.IS_SAVED, true);

        tourParseQuery.findInBackground((_destinationCollections, e) -> {
            if (e != null) {
                Log.e(TAG, "Issues with getting tours from DB. " + e.getMessage());
                return;
            }

            savedTours.addAll(_destinationCollections);
            toursAdapter.notifyDataSetChanged();

            Log.i(TAG, String.valueOf(savedTours.size()));
        });
    }
}