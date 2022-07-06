package com.example.where_to_go;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.where_to_go.fragments.MapFragment;
import com.example.where_to_go.models.Destination;

import org.json.JSONException;
import org.json.JSONObject;

public class DestinationDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DestinationDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        ImageView ivDestinationPhoto = findViewById(R.id.ivDestinationPhoto);
        TextView tvDestinationName = findViewById(R.id.tvDestinationName);
        TextView tvDestinationDetails = findViewById(R.id.tvDestinationDetails); // TODO: Get this
        TextView tvHours = findViewById(R.id.tvHours); // TODO: Calculate this
        RatingBar rbPathRating = (RatingBar) findViewById(R.id.rbPathRating);
        Button btnBack = findViewById(R.id.btnBack);

        // Un-pack the object transferred here.
        // TODO: Input object isn't null, but output is null. FIX IT!
        String strDestination = getIntent().getStringExtra(Destination.class.getSimpleName());
        Destination destination = new Destination();
        try {
            JSONObject jsonDestination = new JSONObject(strDestination);
            Log.i(TAG, jsonDestination.toString());
            destination.setData(jsonDestination);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set properties

        // Set Image
        Log.i(TAG, destination.getImageUrl());
        Glide.with(this).load(destination.getImageUrl()).into(ivDestinationPhoto);

        // Set text
        tvDestinationName.setText(destination.getLocationName());
        tvDestinationDetails.setText("Need Description!");
        tvHours.setText("0");
        rbPathRating.setRating((float) destination.getRating());

        btnBack.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.clDestinationDetails, new MapFragment()).commit();
            finish();
        });
    }
}