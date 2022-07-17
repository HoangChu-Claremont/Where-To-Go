package com.example.where_to_go.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.where_to_go.R;

public class DestinationDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DestinationDetailsActivity";
    private static final String MILES = "miles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        ImageView ivDestinationPhoto = findViewById(R.id.ivDestinationPhoto);
        TextView tvDestinationName = findViewById(R.id.tvDestinationName);
        TextView tvDestinationPhone = findViewById(R.id.tvDestinationPhone);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvDistance = findViewById(R.id.tvDistance);
        RatingBar rbPathRating = findViewById(R.id.rbPathRating);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
            finish();
        });

        setInformation(ivDestinationPhoto, tvDestinationName, tvDestinationPhone, tvAddress, tvDistance, rbPathRating);
    }

    // HELPER METHODS

    private void setInformation(ImageView ivDestinationPhoto, TextView tvDestinationName, TextView tvDestinationPhone, TextView tvAddress, TextView tvDistance, RatingBar rbPathRating) {
        // Un-pack the object transferred here.
        Intent intent = getIntent();

        // Numbers
        double destinationRating = intent.getDoubleExtra("destination_rating", 0.0);
        double destinationDistance = intent.getDoubleExtra("destination_distance", 0.00);

        // Text
        String destinationPhoto = intent.getStringExtra("destination_photo");
        String destinationName = intent.getStringExtra("destination_name");
        String destinationPhone = intent.getStringExtra("destination_phone");
        String destinationAddress = intent.getStringExtra("destination_address");
        String str_destinationDistance = (destinationDistance > 0) ?  // Some destinations have exact LatLng
                String.valueOf(destinationDistance) : "< 1.0";        // indicating a < 1.00-mile distance.

        // Set Image
        Glide.with(this).load(destinationPhoto).into(ivDestinationPhoto);

        // Set text
        tvDestinationName.setText(destinationName);
        tvDestinationPhone.setText(destinationPhone);
        rbPathRating.setRating((float) destinationRating);
        tvDistance.setText(str_destinationDistance + " " + MILES);
        tvAddress.setText(destinationAddress);
    }
}