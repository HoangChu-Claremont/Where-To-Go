package com.example.where_to_go.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.where_to_go.FilterActivity;
import com.example.where_to_go.NavigationActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.adapters.DestinationsAdapter;
import com.example.where_to_go.adapters.ToursAdapter;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.models.Tour;
import com.example.where_to_go.utilities.FilterAlgorithm;
import com.example.where_to_go.utilities.MultiThreadYelpAPI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapFragment";

    private static final Object LOCK = new Object();
    private DestinationsAdapter filteredDestinationAdapter;
    private List<Destination> filteredDestinations;

    RecyclerView rvDestinations;
    Button btnStartSaveTour;
    TextView etTourName;

    JSONObject jsonFilteredResult;
    List<Destination> filteredResults = new ArrayList<>();
    String intent = "Default";

    HashMap<String, JSONArray> categoryDestinationsMap = new HashMap<>();

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
        jsonFilteredResult = _filterResult;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        supportMapFragment.getMapAsync(googleMap -> {
            try {
                getFilteredDestination(googleMap);
                Log.i(TAG, "Map Created");
            } catch (JSONException | IOException | InterruptedException | ParseException e) {
                e.printStackTrace();
            }
        });

        btnStartSaveTour = view.findViewById(R.id.btnStartSave);
        btnStartSaveTour.setOnClickListener(v -> {
            // Set up required variables for querying the DB
            etTourName = view.findViewById(R.id.etTourName);

            String tourName = etTourName.getText().toString();
            ParseUser currentUser = ParseUser.getCurrentUser();
            Log.i(TAG, "Current User: " + currentUser);

            List<String> tourNames = new ArrayList<>();

            // Get a list of existing tour names
            ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
            tourParseQuery.selectKeys(Arrays.asList(Tour.TOUR_NAME));
            try {
                List<Tour> tourFounds = tourParseQuery.find();
                for (Tour tourFound : tourFounds) {
                    tourNames.add(tourFound.getObjectId());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (tourName.isEmpty()) {
                Toast.makeText(getContext(), "Tour name can't be empty", Toast.LENGTH_SHORT).show();
            } else if (tourNames.contains(tourName)) {
                Toast.makeText(getContext(), "Tour name already exists", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    if (ToursAdapter.POSITION == -1) {
                        saveToursToParseDB(tourName, currentUser);
                    }
                    startGoogleDirection(filteredDestinations);
                } catch (ParseException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startGoogleDirection(List<Destination> filteredDestinations) {
        // TODO: Start Google Map Application
        String url = getUrl(filteredDestinations, "driving"); // TODO: Create an enum

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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

    private void saveTour(@NonNull View view) {
        // Set up required variables for querying the DB
        etTourName = view.findViewById(R.id.etTourName);

        String tourName = etTourName.getText().toString();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i(TAG, "Current User: " + currentUser);

        List<String> tourNames = new ArrayList<>();

        // Get a list of existing tour names
        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        tourParseQuery.selectKeys(Arrays.asList(Tour.TOUR_NAME));
        try {
            List<Tour> tourFounds = tourParseQuery.find();
            for (Tour tourFound : tourFounds) {
                tourNames.add(tourFound.getObjectId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (tourName.isEmpty()) {
            Toast.makeText(getContext(), "Tour name can't be empty", Toast.LENGTH_SHORT).show();
        } else if (tourNames.contains(tourName)) {
            Toast.makeText(getContext(), "Tour name already exists", Toast.LENGTH_SHORT).show();
        } else {
            try {
                saveToursToParseDB(tourName, currentUser);
            } catch (ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void goHomeActivity() {
        // Switch between MapFragment -> HomeFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
        if (getActivity() instanceof FilterActivity) {
            getActivity().finish();
        } else if (getActivity() instanceof NavigationActivity) {
            // Reset bottom navigation bar
            BottomNavigationView bottomNavigationView = ((NavigationActivity) requireContext()).bottomNavigationView;
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
    }

    private void getFilteredDestination(GoogleMap googleMap) throws JSONException, IOException, InterruptedException, ParseException {
        Log.i(TAG, "getFilteredDestination");

        List<String> categories = new ArrayList<>();

        if (!intent.equals("Default")) {
            categories = Arrays.asList(jsonFilteredResult.getString("destination_type").split(","));
        } else {
            categories.add("");
        }

        MultiThreadYelpAPI myThread;
        JSONArray jsonResults = new JSONArray();
        for (String category : categories) {
            Log.i(TAG, "Category: " + category);
            myThread = new MultiThreadYelpAPI(category, FilterActivity.currentLongitude, FilterActivity.currentLatitude);
            myThread.start();
            myThread.join();
            jsonResults = myThread.getJsonResults();
            synchronized(this) {
                categoryDestinationsMap.put(category, jsonResults);
            }
        }

        Log.i(TAG, "categoryDestinationsMap: " + categoryDestinationsMap.size());
        Log.i(TAG, "jsonResults: " + jsonResults.length());

        Log.i(TAG, "Clicked on position: " + ToursAdapter.POSITION);

        if (ToursAdapter.POSITION != -1) {
            filteredResults = getDestinationsFromDB(filteredResults);
            ToursAdapter.POSITION = -1; // Reset for next-time classification
        } else {
            if (filteredResults.isEmpty()) {
                if (!intent.equals("Default")) {
                    filteredResults = FilterAlgorithm.getFilteredTour(jsonFilteredResult, categoryDestinationsMap);
                } else {
                    filteredResults = FilterAlgorithm.getTopRatedTour(jsonResults);
                }
            }
        }

        filteredDestinations.addAll(filteredResults);

        // Update the Adapter
        filteredDestinationAdapter.notifyDataSetChanged();

        setGoogleMap(googleMap, filteredDestinations);

        // Users can reorder locations
        setDragDropDestinations(rvDestinations);
    }

    private List<Destination> getDestinationsFromDB(List<Destination> filteredResults) throws ParseException {
        String clickedTourID = getClickedTourID();
        ParseObject obj = ParseObject.createWithoutData(Tour.class, clickedTourID);

        List<Destination> destinationFromDBs = ParseQuery.getQuery(Destination.class)
                .whereEqualTo(Destination.TOUR_ID, obj)
                .find();

        for (Destination destinationFromDB : destinationFromDBs) {
            destinationFromDB.setFieldFromDB();
            filteredResults.add(destinationFromDB);
        }

        return filteredResults;
    }

    private String getClickedTourID(){
        Log.i(TAG, "getDestinationsFromDB");
        List<Destination> resultsFromDB = new ArrayList<>();
        List<String> tourIDs = new ArrayList<>();

        // Get a list of existing tour names
        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        tourParseQuery.selectKeys(Arrays.asList(Tour.OBJECT_ID));
        try {
            List<Tour> tourFounds = tourParseQuery.find();
            for (Tour tourFound : tourFounds) {
                tourIDs.add(tourFound.getObjectId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get the clicked tour, which is the most recently session.
        Log.i(TAG, "tourIDs size: " + tourIDs.size());
        Log.i(TAG, "Position: " + ToursAdapter.POSITION);
        String clickedTourID = tourIDs.get(ToursAdapter.POSITION);
        Log.i(TAG, "clickedTourID: " + clickedTourID);

        return clickedTourID;
    }

    private void setFilteredDestinationRecyclerView() {
        Log.i(TAG, "setFilteredDestinationRecyclerView");

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
            MarkerOptions marker = new MarkerOptions();

            LatLng coordinate = new LatLng(destination.getLatitude(), destination.getLongitude());
            MarkerOptions currentPlace = marker.position(coordinate).title(destination.getLocationName());

            googleMap.addMarker(currentPlace);
            builder.include(marker.getPosition()); // Build bounds
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);
    }

    @NonNull
    private String getUrl(@NonNull List<Destination> filteredDestinations, String directionMode) {
        Log.i(TAG, "Get Url with filteredDestinations size: " + filteredDestinations.size());
        // Set output format
        String outputFormat = "json";

        // Origin of route
        Destination origin = filteredDestinations.get(0);
        String strOrigin = "origin=" + origin.getLatitude() + "%2C" + origin.getLongitude();
        // Destination of route
        Destination dest = filteredDestinations.get(filteredDestinations.size()-1);
        String strDest = "destination=" + dest.getLatitude() + "%2C" + dest.getLongitude();

        // Build the startEnd for the API
        String startEnd = strOrigin + "&" + strDest;

        // Set waypoints
        StringBuilder waypoints = new StringBuilder();
        for (int i = 1; i < filteredDestinations.size()-1 - 1; ++i) { // We don't need '|' for the last one
            Destination waypoint = filteredDestinations.get(i);
            waypoints.append(waypoint.getLatitude()).append("%2C").append(waypoint.getLongitude());
            waypoints.append("%7C");
        }
        Destination finalWaypoint = filteredDestinations.get(filteredDestinations.size()-1-1);
        waypoints.append(finalWaypoint.getLatitude()).append("%2C").append(finalWaypoint.getLongitude());
        Log.i(TAG, "waypoints: " + waypoints);

        // Mode
        String mode = "mode=" + directionMode;

        // Build the url
        String url = "https://maps.googleapis.com/maps/api/directions/" + outputFormat + "?"
                + startEnd + "&" + waypoints + "&" + mode + "&key=" + getString(R.string.google_maps_api_key);

        Log.i(TAG, "URL: " + url);
        return url;
    }

    private void setDragDropDestinations(RecyclerView rvDestinations) {
        Log.i(TAG, "setDragDropDestinations");

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
        Log.i(TAG, "saveToursToParseDB");

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
        Log.i(TAG, "saveToToursDB");

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
        Log.i(TAG, "saveToDestinationsDB: " + objectId);

        // Getting information to set up the POST query
        currentDestination.put("tour_id", ParseObject.createWithoutData(Tour.class, objectId));
        currentDestination.putToDB();

        currentDestination.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving a new destination", e);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}