package com.example.where_to_go;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.where_to_go.fragments.MapFragment;
import com.example.where_to_go.models.Destination;

import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        Button btnStopInfo = findViewById(R.id.btnStopInfo);

        // Un-pack the object transferred here.
        // TODO: Input object isn't null, but output is null. FIX IT!
        Destination destination = Parcels.unwrap(getIntent().getParcelableExtra(Destination.class.getSimpleName()));
        // Set properties

        // Set Image
//        Bitmap takenImage = getBitmapFromURL(destination.getImageUrl());
//        ivDestinationPhoto.setImageBitmap(takenImage);

        // Set text
        Log.i(TAG, destination.getLocationName());
        tvDestinationName.setText(destination.getLocationName());
        tvDestinationDetails.setText("Need Description!");
        tvHours.setText("0");
        rbPathRating.setRating((float) destination.getRating());

        btnStopInfo.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment mapFragment = new MapFragment();
            fragmentManager.beginTransaction().replace(R.id.clDestinationDetails, mapFragment).commit();
        });
    }

    // HELPER METHODS
    @Nullable
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }
}