package com.example.where_to_go.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.where_to_go.R;
import com.example.where_to_go.adapters.DestinationsAdapter;
import com.example.where_to_go.models.Destinations;
import com.example.where_to_go.models.Tours;
import com.example.where_to_go.utilities.FilterAlgorithm;
import com.example.where_to_go.utilities.YelpClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
    private DestinationsAdapter filteredDestinationAdapter;
    private List<Destinations> filteredDestinations;

    RecyclerView rvDestinations;
    Button btnStartSaveTour;
    TextView etTourName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MapFragment() {
        // Required empty public constructor
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

        // Featured Destinations
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
            etTourName = view.findViewById(R.id.etPathName);
            String tourName = etTourName.getText().toString();
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (tourName.isEmpty()) {
                Toast.makeText(getContext(), "Tour name can't be empty", Toast.LENGTH_SHORT).show();
            }

            try {
                saveTourToParseDB(tourName, currentUser);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    // HELPER METHODS

    private void getFilteredDestination(GoogleMap googleMap) {

        final YelpClient yelpClient = new YelpClient();

        yelpClient.getResponse(-122.1483654685629, 37.484668049999996, new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseData = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonData = new JSONObject(responseData);

                    JSONArray jsonResults = jsonData.getJSONArray("businesses");
                    List<Destinations> filteredResults = FilterAlgorithm.getTopRatedTour(jsonResults);
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

        rvDestinations = requireView().findViewById(R.id.rvDestinations);
        // Create the Adapter
        filteredDestinationAdapter = new DestinationsAdapter(getContext(), filteredDestinations);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDestinations.setLayoutManager(tLayoutManager);
        rvDestinations.setHasFixedSize(true); // always get 10 tours maximum

        // Set the Adapter on RecyclerView
        rvDestinations.setAdapter(filteredDestinationAdapter);
    }

    private void setGoogleMap(GoogleMap googleMap, @NonNull List<Destinations> filteredDestinations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int padding = 420; // More values = More zooming out. TODO: Calculate Padding

        // Mark each destination on the Map
        for (Destinations destination : filteredDestinations) {
            LatLng coordinate = new LatLng(destination.getLatitude(), destination.getLongitude());
            MarkerOptions marker = new MarkerOptions();
            googleMap.addMarker(marker.position(coordinate).title(destination.getTitle()));
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

    private void saveTourToParseDB(String tourName, ParseUser currentUser) throws ParseException {
        saveToToursDB(tourName, currentUser);
        for (Destinations filteredDestination : filteredDestinations) {
          saveToDestinationsDB(filteredDestination);
        }
    }

    private void saveToToursDB(String pathName, @NonNull ParseUser currentUser) {
        Tours destinationCollections = new Tours();
        // Getting information to set up the POST query
        destinationCollections.put("user_id", ParseObject.createWithoutData(ParseUser.class, currentUser.getObjectId()));
        destinationCollections.setTourName(pathName);
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
    }

    private void saveToDestinationsDB(@NonNull Destinations filteredDestination) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tours");
        String objectId = query.addDescendingOrder("created_at").find().get(0).getObjectId();

        filteredDestination.put("tour_id", ParseObject.createWithoutData(Tours.class, objectId));
        filteredDestination.putToDB();
        filteredDestination.saveInBackground(e -> {
            if (e != null) {
                Log.i(TAG, "Problem saving this destination");
            } else {
                Log.i(TAG, "Saved a new destination successfully!");
            }
        });
    }
}