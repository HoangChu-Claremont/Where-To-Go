package com.example.where_to_go.fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.where_to_go.MapActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.adapters.FilteredDestinationAdapter;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.utilities.FilterAlgorithm;
import com.example.where_to_go.utilities.YelpClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    private FilteredDestinationAdapter filteredDestinationAdapter;
    private List<Destination> filteredDestinations;
//    private GoogleMap googleMap;

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

        // Featured Destination
        setFilteredDestinationRecyclerView();

        // Get a handle to the fragment and register the callback.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        // When Google Map is loaded, test that we captured the fragment
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync( googleMap -> {
            getFilteredDestination(googleMap);
            // When clicking on map
//            LatLng sydney = new LatLng(-33.852, 151.211);
//            googleMap.addMarker(new MarkerOptions()
//                    .position(sydney)
//                    .title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        });

        Log.i(TAG, "Map Created");


//        setGoogleMap();
    }


    // HELPER METHODS

    private void getFilteredDestination(GoogleMap googleMap) {

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

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        for (Destination destination : filteredDestinations) {
                            LatLng coordinate = new LatLng(destination.getLatitude(), destination.getLongitude());
                            MarkerOptions marker = new MarkerOptions();
                            googleMap.addMarker(marker.position(coordinate).title(destination.getTitle()));
                            builder.include(marker.getPosition());
                        }

                        LatLngBounds bounds = builder.build();
                        // TODO: Calculate Padding
                        int padding = 420; // More values = More zooming out
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.moveCamera(cu);
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
        // query for top 10 paths based on average
        filteredDestinations = new ArrayList<>();

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

    private void setGoogleMap() {
        startActivity(new Intent(getContext(), MapActivity.class));
        requireActivity().finish();
    }
}