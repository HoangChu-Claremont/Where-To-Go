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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.where_to_go.FilterActivity;
import com.example.where_to_go.MainActivity;
import com.example.where_to_go.NavigationActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.adapters.DestinationsAdapter;
import com.example.where_to_go.adapters.ToursAdapter;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.models.Tour;
import com.example.where_to_go.utilities.DatabaseUtils;
import com.example.where_to_go.utilities.FilterAlgorithm;
import com.example.where_to_go.utilities.MultiThreadYelpAPI;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, DestinationsAdapter.NavigationAdapter {
    private static final String TAG = "MapFragment";
    private static final Object LOCK = new Object();

    private DestinationsAdapter filteredDestinationAdapter;
    private List<Destination> filteredDestinations;
    private RecyclerView rvDestinations;
    private Button btnStartSaveTour;
    private EditText etTourName;
    private JSONObject jsonFilteredResult;
    private String intent = "Default";
    private List<Marker> currentMarkers;

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
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filteredDestinations = new ArrayList<>();

        // Set up appropriate view
        setStartSaveButton(view);
        setUpGoogleMap();
        setFilteredDestinationRecyclerView();

        etTourName = view.findViewById(R.id.etTourName);

        btnStartSaveTour = view.findViewById(R.id.btnStartSave);
        btnStartSaveTour.setOnClickListener(v -> {
            try {
                startSaveAction();
                updateTourInHomeFragment();

                synchronized (LOCK) {
                    LOCK.wait(3000);
                }

                goHomeActivity();

            } catch (Exception e) {
                Log.i(TAG, "Can't start / save." + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void updateTourInHomeFragment() {
        List<Fragment> fragments = getFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            Log.i(TAG, "Previous Fragment: " + fragment.getTag());
        }
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

    private void setStartSaveButton(View view) {
        if (ToursAdapter.POSITION != -1) {
            EditText etSavedTourName = view.findViewById(getResources().getIdentifier("etTourName", "id",
                    requireActivity().getPackageName()));
            etSavedTourName.setVisibility(View.GONE);
        }
    }

    private void setUpGoogleMap() {
        currentMarkers = new ArrayList<>();

        // Get a handle to the fragment and register the callback.
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        // When Google Map is loaded, test that we captured the fragment
        assert supportMapFragment != null;

        // Method reference: Google Map shows asynchronously with filtered data.
        supportMapFragment.getMapAsync(googleMap -> {
            getFilteredDestination(googleMap);
            Log.i(TAG, "Map Created");
        });
    }

    private void startGoogleDirection(List<Destination> filteredDestinations) {
        // TODO: Start Google Map Application
        String url = getUrl(filteredDestinations, "driving"); // TODO: Create an enum

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }

    private void startSaveAction() {
        Log.i(TAG, "startSaveAction");

        String tourName = etTourName.getText().toString();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i(TAG, "Current User: " + currentUser);

        // Get a list of existing tour names
        List<String> tourNames = getExistingTourNamesInDB(new ArrayList<>());

        if (ToursAdapter.POSITION == -1) { // Destinations after being filtered
            if (tourName.isEmpty()) {
                Toast.makeText(getContext(), "Tour name can't be empty", Toast.LENGTH_SHORT).show();
            } else if (tourNames.contains(tourName)) {
                Toast.makeText(getContext(), "Tour name already exists", Toast.LENGTH_SHORT).show();
            } else if (filteredDestinations.size() == 0) {
                Toast.makeText(getContext(), "There is no destinations to save", Toast.LENGTH_SHORT).show();
            } else {
                saveToursToParseDB(tourName, currentUser);
                Log.i(TAG, "Start Google Maps's Directions");
                startGoogleDirection(filteredDestinations);
            }
        } else { // Featured or saved or seen destinations
            Log.i(TAG, "Start Google Maps's Directions");
            startGoogleDirection(filteredDestinations);
        }
    }

    private List<String> getExistingTourNamesInDB(List<String> tourNames) {
        Log.i(TAG, "getExistingTourNamesInDB");

        List<Tour> returnedTours = DatabaseUtils.getAllToursFromDatabase();

        for (Tour returnedTour : returnedTours) {
            tourNames.add(returnedTour.getTourNameDB());
        }

        return tourNames;
    }

    @Override
    public void goHomeActivity() {
        Log.i(TAG, "goHomeActivity");

        // Switch between MapFragment -> HomeFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();

        // Reset bottom navigation bar if previous Activity was NavigationActivity
        if (getActivity() instanceof FilterActivity) {
            getActivity().finish();
        } else if (getActivity() instanceof NavigationActivity) {
            BottomNavigationView bottomNavigationView = ((NavigationActivity) requireContext()).bottomNavigationView;
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }

        ToursAdapter.POSITION = -1;
    }

    @NonNull
    private Pair<JSONArray, HashMap<String, JSONArray>> getJSonResultsFromYelpAndBuiltCategoryDestinationsMap(@NonNull String intent) {
        Log.i(TAG, "getJSonResultsFromYelpAndBuiltCategoryDestinationsMap");

        List<String> categories = new ArrayList<>();
        JSONArray jsonResults = new JSONArray();
        HashMap<String, JSONArray> categoryDestinationsMap = new HashMap<>();

        MultiThreadYelpAPI yelpAPIRequestThread;

        // Get all categories for the map
        if (!intent.equals("Default")) {
            try {
                categories = Arrays.asList(jsonFilteredResult.getString("destination_type").split(","));
            } catch (JSONException e) {
                Log.i(TAG, e.getMessage());
            }
        } else {
            categories.add("");
        }

        // Fetch all JSON results from YelpAPI
        for (String category : categories) {
            Log.i(TAG, "Category: " + category);
            yelpAPIRequestThread = new MultiThreadYelpAPI(category, MainActivity.CURRENT_LONGITUDE, MainActivity.CURRENT_LATITUDE);
            yelpAPIRequestThread.start();
            try {
                yelpAPIRequestThread.join();
            } catch (InterruptedException e) {
                Log.i(TAG, e.getMessage());
            }
            jsonResults = yelpAPIRequestThread.getJsonResults();
            synchronized(this) {
                categoryDestinationsMap.put(category, jsonResults);
            }
        }

        Log.i(TAG, "categoryDestinationsMap: " + categoryDestinationsMap.size());
        Log.i(TAG, "jsonResults: " + jsonResults.length());
        return new Pair<>(jsonResults, categoryDestinationsMap);
    }

    private void getFilteredDestination(GoogleMap googleMap) {
        Log.i(TAG, "getFilteredDestination");

        if (ToursAdapter.POSITION != -1) {
            filteredDestinations.addAll(getDestinationsFromExistingTours());
        } else {
            if (filteredDestinations.isEmpty()) {
                Pair<JSONArray, HashMap<String, JSONArray>> returnedPair =  getJSonResultsFromYelpAndBuiltCategoryDestinationsMap(intent);
                JSONArray jsonResults = returnedPair.first;
                HashMap<String, JSONArray> categoryDestinationsMap = returnedPair.second;

                // Fetch destinations from YelpAPI
                try {
                    if (!intent.equals("Default")) {
                        List<Destination> filteredResults = FilterAlgorithm.getFilteredTour(jsonFilteredResult, categoryDestinationsMap);
                        filteredDestinations.addAll(filteredResults);
                    } else {
                        List<Destination> defaultResults = FilterAlgorithm.getTopRatedTour(jsonResults);
                        filteredDestinations.addAll(defaultResults);
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "Can't fetch destinations from Yelp. " + e.getMessage());
                }
            }
        }

        // Update the Adapter
        filteredDestinationAdapter.notifyDataSetChanged();

        setGoogleMapAndAddMarkers(googleMap, filteredDestinations);

        // Users can reorder locations
        setDragDropDestinations(rvDestinations);
    }

    @NonNull
    private List<Destination> getDestinationsFromExistingTours() {
        List<Destination> destinationsFromExistingTours;

        String clickedTourID = getClickedTourID();
        Log.i(TAG, "clickedTourID: " + clickedTourID);

        List<Destination> destinationsFromDB = DatabaseUtils.getAllUnPackedDestinationsFromATour(clickedTourID);
        destinationsFromExistingTours = unPack(destinationsFromDB);

        return destinationsFromExistingTours;
    }

    @NonNull
    private List<Destination> unPack(@NonNull List<Destination> destinationsFromDB) {
        Log.i(TAG, "unPacking...");

        List<Destination> unPackedDestinations  = new ArrayList<>();

        for (Destination destinationFromDB : destinationsFromDB) {
            destinationFromDB.getFieldFromDB();
            unPackedDestinations.add(destinationFromDB);
        }

        return unPackedDestinations;
    }

    private String getClickedTourID(){
        Log.i(TAG, "getDestinationsFromDB");
        List<String> tourIDs = new ArrayList<>();

        // Get a list of existing tour names
        ParseQuery<Tour> tourParseQuery = ParseQuery.getQuery(Tour.class);
        tourParseQuery.addDescendingOrder("updatedAt")
                .selectKeys(Collections.singletonList(Tour.KEY_OBJECT_ID));
        try {
            List<Tour> tourFounds = tourParseQuery.find();
            for (Tour tourFound : tourFounds) {
                tourIDs.add(tourFound.getObjectId());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get the clicked tour, which is the most recently session.
        Log.i(TAG, "tourIDs: " + tourIDs);
        Log.i(TAG, "clicked tour position: " + ToursAdapter.POSITION);
        String clickedTourID = tourIDs.get(ToursAdapter.POSITION);
        Log.i(TAG, "clickedTourID: " + clickedTourID);

        return clickedTourID;
    }

    private void setFilteredDestinationRecyclerView() {
        Log.i(TAG, "setFilteredDestinationRecyclerView");

        rvDestinations = requireView().findViewById(R.id.rvDestinations);

        // Create the Adapter
        filteredDestinationAdapter = new DestinationsAdapter(getContext(), filteredDestinations, currentMarkers, this);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDestinations.setLayoutManager(tLayoutManager);

        // Set the Adapter on RecyclerView
        rvDestinations.setAdapter(filteredDestinationAdapter);
    }

    @NonNull
    private void setGoogleMapAndAddMarkers(GoogleMap googleMap, @NonNull List<Destination> filteredDestinations) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<Marker> markers = new ArrayList<>();

        int padding = 420; // More values = More zooming out. TODO: Calculate Padding

        // Mark each destination on the Map
        for (Destination destination : filteredDestinations) {
            MarkerOptions markerOptions = new MarkerOptions();

            LatLng coordinate = new LatLng(destination.getLatitude(), destination.getLongitude());
            MarkerOptions currentPlace = markerOptions.position(coordinate).title(destination.getLocationName());

            Marker marker = googleMap.addMarker(currentPlace);
            currentMarkers.add(marker);

            builder.include(markerOptions.getPosition()); // Build bounds
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);

        Log.i(TAG, "markers size: " + markers.size());
    }

    @NonNull
    private String getUrl(@NonNull List<Destination> filteredDestinations, String directionMode) {
        Log.i(TAG, "Get Url with filteredDestinations size: " + filteredDestinations.size());

        Log.i(TAG, "Current Latitude: " + MainActivity.CURRENT_LATITUDE);
        Log.i(TAG, "Current Longitude: " + MainActivity.CURRENT_LONGITUDE);

        // Origin of route
        String strOrigin = "origin=" + MainActivity.CURRENT_LATITUDE + "%2C" + MainActivity.CURRENT_LONGITUDE;
        // Destination of route
        String strDest = "destination=" + MainActivity.CURRENT_LATITUDE + "%2C" + MainActivity.CURRENT_LONGITUDE;

        // Build the startEnd for the API
        String startEnd = strOrigin + "&" + strDest;

        // Set waypoints
        StringBuilder waypoints = new StringBuilder("waypoints=");
        String pipeSymbol = "%7C";
        String commaSymbol = "%2C";
        for (int i = 0; i < filteredDestinations.size(); ++i) { // We don't need '|' for the last one
            Destination waypoint = filteredDestinations.get(i);
            waypoints.append(waypoint.getLatitude()).append(commaSymbol).append(waypoint.getLongitude());
            waypoints.append(pipeSymbol);
        }
        waypoints.substring(0, waypoints.length() - pipeSymbol.length());
        Log.i(TAG, "waypoints: " + waypoints);

        // Mode
        String mode = "travelmode=" + directionMode;

        // Build the url
        String url = "https://www.google.com/maps/dir/?api=1&"
                + startEnd + "&" + waypoints + "&" + mode + "&key=" + getString(R.string.google_maps_api_key);

        Log.i(TAG, "URL: " + url);
        return url;
    }

    private void setDragDropDestinations(RecyclerView rvDestinations) {
        Log.i(TAG, "setDragDropDestinations");

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                filteredDestinationAdapter.onItemMove(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    filteredDestinationAdapter.onItemRemove(position);
                    MapFragment.resetMarkers(currentMarkers, position);
                }

                if (filteredDestinationAdapter.getItemCount() == 0) {
                    goHomeActivity();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvDestinations);
    }

    public static void resetMarkers(@NonNull List<Marker> currentMarkers, int position) {
        currentMarkers.get(position).setVisible(false);
        currentMarkers.remove(position);
    }

    private void saveToursToParseDB(String tourName, ParseUser currentUser) {
        Log.i(TAG, "saveToursToParseDB");

        // Save tour to DB
        String savedTourId = DatabaseUtils.saveOneTourToDatabaseAndReturnID(tourName, currentUser);
        if (savedTourId.isEmpty()) {
            Toast.makeText(getContext(), "Error while saving your tour :(", Toast.LENGTH_SHORT).show();
            return;
        }

        // Wait for 3 seconds for TourDB adding a new row.

        try {
            synchronized (LOCK) {
                LOCK.wait(3000);
            }
        } catch (Exception e) {
            Log.i(TAG, "Can't wait. " + e.getMessage());
        }

        // Save destinations to DB accordingly
        boolean hasSaveDestinationsFromTour = DatabaseUtils.saveDestinationsToDatabase(filteredDestinations, savedTourId);
        if (hasSaveDestinationsFromTour) {
            etTourName.setText("");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}