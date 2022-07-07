package com.example.where_to_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    boolean locationPermissionGranted = false;
    public static double currentLongitude, currentLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (hasPermission()) {
            Log.i(TAG, "Getting current location...");
            getDeviceLocation();
        } else {
            Log.i(TAG, "Requesting location permission...");
            requestPermissions();
        }
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // HELPER METHODS
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) { // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Toast.makeText(this, "Please allow location permission for your best experience", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                currentLongitude = location.getLongitude();
                currentLatitude = location.getLatitude();
                Log.i(TAG, "Current Longitude: " + currentLongitude);
                Log.i(TAG, "Current Latitude: " + currentLatitude);
            }

            navigateToFragment();
        });
    }

    private void navigateToFragment() {
        if (ParseUser.getCurrentUser() == null) {
            // If someone has logged out already
            Log.i(TAG, "goLogin");
            goLoginActivity();
        } else {
            Log.i(TAG, "goHome");
            goHomeActivity();
        }
    }

    private void goHomeActivity() {
        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
        finish();
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}