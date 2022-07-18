package com.example.where_to_go.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.where_to_go.R;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class DestinationDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DestinationDetailsActivity";
    private static final String MILES = "miles";
    private double destinationLongitude, destinationLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        TextView tvDestinationName = findViewById(R.id.tvDestinationName);
        TextView tvDestinationPhone = findViewById(R.id.tvDestinationPhone);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvDistance = findViewById(R.id.tvDistance);
        RatingBar rbPathRating = findViewById(R.id.rbPathRating);
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.street_view_panorama); // Street View

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
            finish();
        });

        setInformation(tvDestinationName, tvDestinationPhone, tvAddress, tvDistance, rbPathRating);

        assert streetViewPanoramaFragment != null;
        implementStreetViewPanorama(streetViewPanoramaFragment);
    }

    // HELPER METHODS

    private void setInformation(@NonNull TextView tvDestinationName, @NonNull TextView tvDestinationPhone,
                                @NonNull TextView tvAddress, @NonNull TextView tvDistance, @NonNull RatingBar rbPathRating) {

        // Un-pack the object transferred here.
        Intent intent = getIntent();

        // Set Numbers
        double destinationRating = intent.getDoubleExtra("destination_rating", 0.0);
        double destinationDistance = intent.getDoubleExtra("destination_distance", 0.00);
        destinationLongitude = Double.parseDouble(intent.getStringExtra("destination_longitude"));
        destinationLatitude = Double.parseDouble(intent.getStringExtra("destination_latitude"));

        testExtractedNumbers(destinationRating, destinationDistance);

        // Set Text
        String destinationName = intent.getStringExtra("destination_name");
        String destinationPhone = intent.getStringExtra("destination_phone");
        String destinationAddress = intent.getStringExtra("destination_address");
        String str_destinationDistance = (destinationDistance > 0) ?  // Some destinations have exact LatLng
                String.valueOf(destinationDistance) : "< 1.0";        // indicating a < 1.00-mile distance.

        tvDestinationName.setText(destinationName);
        tvDestinationPhone.setText(destinationPhone);
        rbPathRating.setRating((float) destinationRating);
        tvDistance.setText(str_destinationDistance + " " + MILES);
        tvAddress.setText(destinationAddress);
    }

    private void testExtractedNumbers(double destinationRating, double destinationDistance) {
        Log.i(TAG, "testExtractedNumbers");

        Log.i(TAG, "destinationRating: " + destinationRating);
        Log.i(TAG, "destinationDistance: " + destinationDistance);
        Log.i(TAG, "Destination longitude: " + destinationLongitude);
        Log.i(TAG, "Destination latitude: " + destinationLatitude);
    }

    private void implementStreetViewPanorama(@NonNull SupportStreetViewPanoramaFragment streetViewPanoramaFragment) {
        Log.i(TAG, "implementStreetViewPanorama");

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(streetViewPanorama -> {
            LatLng currentDestination = new LatLng(destinationLatitude, destinationLongitude);

            streetViewPanorama.setPosition(currentDestination);

            long duration = 1000;
            StreetViewPanoramaCamera camera =
                    new StreetViewPanoramaCamera.Builder()
                            .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                            .tilt(streetViewPanorama.getPanoramaCamera().tilt)
                            .bearing(streetViewPanorama.getPanoramaCamera().bearing - 60)
                            .build();
            streetViewPanorama.animateTo(camera, duration);
        });
    }
}