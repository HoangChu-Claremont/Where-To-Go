package com.example.where_to_go.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.where_to_go.FilterActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.adapters.ToursAdapter;
import com.example.where_to_go.models.Tour;
import com.example.where_to_go.utilities.DatabaseUtils;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private ToursAdapter featuredTourAdapter;
    private ToursAdapter recentTourAdapter;
    private List<Tour> featuredTours;
    private List<Tour> recentTours;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        setUpLatestLayout(requireView());
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
        setUpLatestLayout(view);
    }

    // HELPER METHODS

    private void setUpLatestLayout(@NonNull View view) {
        CardView cvContinueTour = view.findViewById(R.id.cvContinueTour);

        cvContinueTour.setOnClickListener(v -> {
            try {
                goFilterActivity();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

        // Setting up RecyclerView
        setFeaturedToursRecyclerView();
        setRecentToursRecyclerView();

        // Get Tour
        getFeaturedTours();
        getRecentTours();
    }

    private void goFilterActivity() {
        Log.i(TAG, "goFilterActivity");

        Intent intent = new Intent(getActivity(), FilterActivity.class);
        final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction().addToBackStack(TAG).commit();
        startActivity(intent);
    }

    private void setRecyclerView(@NonNull RecyclerView recyclerView, ToursAdapter toursAdapter) {
        Log.i(TAG, "setRecyclerView");

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(tLayoutManager);
        recyclerView.setHasFixedSize(true);

        // Set the Adapter on RecyclerView
        recyclerView.setAdapter(toursAdapter);
    }

    private void setFeaturedToursRecyclerView() {
        Log.i(TAG, "setFeaturedToursRecyclerView");

        RecyclerView rvFeaturedTours = requireView().findViewById(R.id.rvFeaturedTours);

        featuredTours = new ArrayList<>();
        featuredTourAdapter = new ToursAdapter(getContext(), featuredTours);

        setRecyclerView(rvFeaturedTours, featuredTourAdapter);
    }

    private void setRecentToursRecyclerView() {
        Log.i(TAG, "setRecentToursRecyclerView");

        RecyclerView rvRecentTours = requireView().findViewById(R.id.rvRecentTours);
        recentTours = new ArrayList<>();
        recentTourAdapter = new ToursAdapter(getContext(), recentTours);

        setRecyclerView(rvRecentTours, recentTourAdapter);
    }

    private void getFeaturedTours() {
        Log.i(TAG, "getFeaturedTours");

        featuredTours.addAll(DatabaseUtils.getFeaturedToursFromDatabase());
        featuredTourAdapter.notifyDataSetChanged();
    }

    private void getRecentTours() {
        Log.i(TAG, "getRecentTours");

        int limit = 5;

        recentTours.addAll(DatabaseUtils.getLimitedRecentToursFromDatabase(limit));
        recentTourAdapter.notifyDataSetChanged();
    }
}