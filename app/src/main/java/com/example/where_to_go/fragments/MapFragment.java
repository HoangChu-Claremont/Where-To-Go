package com.example.where_to_go.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.where_to_go.R;
import com.example.where_to_go.adapters.FilteredDestinationAdapter;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.utilities.FilterAlgorithm;
import com.example.where_to_go.utilities.YelpClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends Fragment {

    private FilteredDestinationAdapter filteredDestinationAdapter;
    private List<Destination> filteredDestinations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(), "You're in Map!", Toast.LENGTH_SHORT).show();

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        // When Google Map is loaded, test that we captured the fragment
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(googleMap -> {
            // When clicking on map
            googleMap.setOnMapClickListener(latLng -> {
                MarkerOptions markerOptions = new MarkerOptions();
                // Set position on Marker
                markerOptions.position(latLng);
                // Set title of the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                // Remove all marker
                googleMap.clear();
                // Animating to Zoom the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                // Add marker on Map
                googleMap.addMarker(markerOptions);
            });
        });

        // Featured Destination
        getFilteredDestination();
        setFilteredDestinationRecyclerView();
    }

    // HELPER METHODS

    private void getFilteredDestination() {

        // query for top 10 paths based on average
        filteredDestinations = new ArrayList<>();
        final YelpClient topPath = new YelpClient();

        topPath.getResponse(-122.1483654685629, 37.484668049999996, 10, new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseData = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonData = new JSONObject(responseData);

                    JSONArray jsonResults = jsonData.getJSONArray("businesses");
                    filteredDestinations.addAll(FilterAlgorithm.getTopRatedPath(jsonResults));

                    // Avoid the "Only the original thread that created a view hierarchy
                    // can touch its views adapter" error
                    ((Activity) requireContext()).runOnUiThread(() -> {
                        //change View Data
                        filteredDestinationAdapter.notifyDataSetChanged();
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setFilteredDestinationRecyclerView() {
        RecyclerView rvDestinations = requireView().findViewById(R.id.rvDestinations);
        // Create the Adapter
        filteredDestinationAdapter = new FilteredDestinationAdapter(getContext(), filteredDestinations);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDestinations.setLayoutManager(tLayoutManager);
        rvDestinations.setHasFixedSize(true); // always get top 10 paths

        // Set the Adapter on RecyclerView
        rvDestinations.setAdapter(filteredDestinationAdapter);
    }
}