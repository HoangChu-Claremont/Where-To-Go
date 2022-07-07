package com.example.where_to_go.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.where_to_go.DestinationDetailsActivity;
import com.example.where_to_go.MainActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Destination;

import java.util.Collections;
import java.util.List;

public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.FilteredDestinationViewHolder> {
    private static final String TAG = "DestinationsAdapter";
    public Context context;
    private final List<Destination> destinations;

    public DestinationsAdapter(Context _context, List<Destination> _destinations) {
        context = _context;
        destinations = _destinations;
    }

    @NonNull
    @Override
    public FilteredDestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topTourView = LayoutInflater.from(context).inflate(R.layout.collection, parent, false);
        return new FilteredDestinationViewHolder(topTourView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilteredDestinationViewHolder holder, int position) {
        Destination destination = destinations.get(position);
        holder.bind(destination);
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int leftWard = fromPosition; leftWard < toPosition; ++leftWard) {
                Collections.swap(destinations, leftWard, leftWard + 1);
            }
        } else {
            for (int rightWard = fromPosition; rightWard > toPosition; --rightWard) {
                Collections.swap(destinations, rightWard, rightWard - 1);
            }
        }

        Log.i(TAG, "Swap item " + fromPosition + " with item " + toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class FilteredDestinationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FeaturedTourViewHolder";
        private final ImageView ivDestinationImage;
        private final TextView tvDestinationName;

        public FilteredDestinationViewHolder(View itemView) {
            super(itemView);

            ivDestinationImage = itemView.findViewById(R.id.ivTourImage);
            tvDestinationName = itemView.findViewById(R.id.tvTourName);
            
            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Destination filteredDestination) {
            tvDestinationName.setText(filteredDestination.getLocationName());
            Glide.with(context).load(filteredDestination.getImageUrl()).into(ivDestinationImage);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            Log.i(TAG, String.valueOf(position));
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position, this won't work if the class is static
                Destination destination = destinations.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, DestinationDetailsActivity.class);

                addInformationToIntent(intent, destination);

                goToDestinationDetails(intent);
            }
        }

        // HELPER METHODS

        private void goToDestinationDetails(Intent intent) {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.commit();
            context.startActivity(intent);
        }

        private Intent addInformationToIntent(@NonNull Intent _intent, @NonNull Destination destination) {
            // Add information to the intent
            _intent.putExtra("destination_photo", destination.getImageUrl());
            _intent.putExtra("destination_name", destination.getLocationName());
            _intent.putExtra("destination_phone", destination.getPhone());
            _intent.putExtra("destination_rating", destination.getRating());

            // Add distance
            destination.setDistance(MainActivity.currentLongitude, MainActivity.currentLatitude);
            _intent.putExtra("destination_distance", destination.getDistance());
            _intent.putExtra("destination_address", destination.getAddress());

            return _intent;
        }
    }
}
