package com.example.where_to_go.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.where_to_go.NavigationActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.adapters.DestinationsAdapter;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.models.Tour;
import com.example.where_to_go.utilities.FilterAlgorithm;
import com.example.where_to_go.utilities.YelpClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
    private static final String TAG = "MapFragment";
    private static final Object LOCK = new Object();
    private DestinationsAdapter filteredDestinationAdapter;
    private List<Destination> filteredDestinations;

    RecyclerView rvDestinations;
    Button btnStartSaveTour;
    TextView etTourName;

    JSONObject filterResult;
    String intent = "Default";

    private List<Destination> filteredResults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public MapFragment() {
        // Required empty public constructor
    }

    public MapFragment(String _intent, JSONObject _filterResult) {
        intent = _intent;
        filterResult = _filterResult;
        Log.i(TAG, intent);
        Log.i(TAG, filterResult.toString());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        // Method reference: Google Map shows asynchronously with filtered data.
        supportMapFragment.getMapAsync(this::getFilteredDestination);
        Log.i(TAG, "Map Created");

        btnStartSaveTour = view.findViewById(R.id.btnStartSave);
        btnStartSaveTour.setOnClickListener(v -> {
            // Set up required variables for querying the DB
            etTourName = view.findViewById(R.id.etTourName);

            String tourName = etTourName.getText().toString();
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (tourName.isEmpty()) {
                Toast.makeText(getContext(), "Tour name can't be empty", Toast.LENGTH_SHORT).show();
            }

            try {
                saveToursToParseDB(tourName, currentUser);
            } catch (ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Button clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_back) {
            Log.i(TAG, "onClick Back Button");
            goHomeActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // HELPER METHODS

    private void goHomeActivity() {
        // Switch between MapFragment -> HomeFragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.map_fragment, new HomeFragment());
        fragmentManager.popBackStack();
        transaction.commit();

        // Reset bottom navigation bar
        BottomNavigationView bottomNavigationView = ((NavigationActivity) requireContext()).bottomNavigationView;
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    private void getFilteredDestination(GoogleMap googleMap) {

        final YelpClient yelpClient = new YelpClient();

        yelpClient.getResponse(-122.1483654685629, 37.484668049999996, new Callback() { // TODO: Get user's current location

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseData = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonData = new JSONObject(responseData);

                    JSONArray jsonResults = jsonData.getJSONArray("businesses");

                    if (intent == "Default") {
                        filteredResults = FilterAlgorithm.getTopRatedTour(jsonResults);
                        filteredDestinations.addAll(filteredResults);

                        // Avoid the "Only the original thread that created a view hierarchy
                        // can touch its views adapter" error
                        ((Activity) requireContext()).runOnUiThread(() -> {
                            // Update the Adapter
                            filteredDestinationAdapter.notifyDataSetChanged();

                            setGoogleMap(googleMap, filteredDestinations);

                            // Users can reorder locations
                            setDragDropDestinations(rvDestinations);

                        });
                    }

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
        filteredDestinations = new ArrayList<>();
        rvDestinations = requireView().findViewById(R.id.rvDestinations);

        // Create the Adapter
        filteredDestinationAdapter = new DestinationsAdapter(getContext(), filteredDestinations);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDestinations.setLayoutManager(tLayoutManager);

        // Set the Adapter on RecyclerView
        rvDestinations.setAdapter(filteredDestinationAdapter);
    }

    private void setGoogleMap(GoogleMap googleMap, @NonNull List<Destination> filteredDestinations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 420; // More values = More zooming out. TODO: Calculate Padding

        // Mark each destination on the Map
        for (Destination destination : filteredDestinations) {
            LatLng coordinate = new LatLng(destination.getLatitude(), destination.getLongitude());
            MarkerOptions marker = new MarkerOptions();
            googleMap.addMarker(marker.position(coordinate).title(destination.getLocationName()));
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);
    }

    private void setDragDropDestinations(RecyclerView rvDestinations) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                filteredDestinationAdapter.onItemMove(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvDestinations);
    }

    private void saveToursToParseDB(String tourName, ParseUser currentUser) throws ParseException, InterruptedException {
        Tour destinationCollections = saveToToursDB(tourName, currentUser);

        // Wait for 3 seconds for TourDB adding a new row.
        synchronized (LOCK) {
            LOCK.wait(3000);
        }

        for (Destination destination : filteredDestinations) {
            saveToDestinationsDB(destination, destinationCollections);
        }
    }

    @NonNull
    private Tour saveToToursDB(String tourName, @NonNull ParseUser currentUser) {
        Tour destinationCollections = new Tour();
				
        // Getting information to set up the POST query
        destinationCollections.put("user_id", ParseObject.createWithoutData(ParseUser.class, currentUser.getObjectId()));
        destinationCollections.setTourName(tourName);
        destinationCollections.setTransportationSeconds(0); // TODO: Create an algorithm to calculate this

        destinationCollections.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving a new tour", e);
                Toast.makeText(getContext(), "Error while saving your tour :(", Toast.LENGTH_SHORT).show();
            }
            Log.i(TAG, "Saved a new tour successfully!");
            etTourName.setText("");
            Toast.makeText(getContext(), "Your tour was saved successfully!", Toast.LENGTH_SHORT).show();
        });

        return destinationCollections;
    }

    private void saveToDestinationsDB(@NonNull Destination currentDestination, @NonNull Tour currentTour) {
        String objectId = currentTour.getObjectId();
        Log.i(TAG, objectId);

        // Getting information to set up the POST query
        currentDestination.put("tour_id", ParseObject.createWithoutData(Tour.class, objectId));
        currentDestination.putToDB();

        currentDestination.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving a new destination", e);
            }
        });
    }
}