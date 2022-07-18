package com.example.where_to_go.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.where_to_go.R;
import com.example.where_to_go.fragments.HomeFragment;
import com.example.where_to_go.fragments.MapFragment;
import com.example.where_to_go.fragments.ProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

public class NavigationActivity extends AppCompatActivity {

    private static final String TAG = "NavigationActivity";

    public BottomNavigationView bottomNavigationView;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!hasPermission()) {
            Log.i(TAG, "Requesting location permission...");
            requestPermissions();
        } else {
//            getDeviceLocation();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            Fragment fragment;
            String fragmentTag;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        fragmentTag = "HomeFragment";
                        break;
                    case R.id.action_map:
                        fragment = new MapFragment();
                        fragmentTag = "MapFragment";
                        break;
                    case R.id.action_profile:
                    default:
                        fragment = new ProfileFragment();
                        fragmentTag = "ProfileFragment";
                        break;
                }
                goAppropriateFragment(item, fragment, fragmentTag);
                return true;
            }
        });
        // Set default in bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }

    // Button clicks
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected");

        if (item.getItemId() == R.id.action_logout) {
            Log.i(TAG, "onClick Logout Button");
            ParseUser.logOutInBackground();
            goLoginActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // HELPER METHODS

    private void goAppropriateFragment(@NonNull MenuItem menuItem, Fragment fragment, String fragmentTag) {
        Log.i(TAG, "goAppropriateFragment");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContainer, fragment, fragmentTag)
                .addToBackStack(String.valueOf(menuItem.getItemId())).commit();
    }

    private void goLoginActivity() {
        Log.i(TAG, "goLoginActivity");

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        Log.i(TAG, "getDeviceLocation");

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                    @NonNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                        Log.i("CurrentLocation", "CancelRequest");
                        getLocationFromPassiveProvide();
                        return null;
                    }

                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }
                })
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        MainActivity.CURRENT_LONGITUDE = location.getLongitude();
                        MainActivity.CURRENT_LATITUDE = location.getLatitude();
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getLocationFromPassiveProvide() {
        Log.i(TAG, "getLocationFromPassiveProvide");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);;
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        MainActivity.CURRENT_LONGITUDE = location.getLongitude();
        MainActivity.CURRENT_LATITUDE = location.getLatitude();
    }

    private boolean hasPermission() {
        Log.i(TAG, "hasPermission");

        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        Log.i(TAG, "requestPermissions");

        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);
    }
}