package com.example.where_to_go.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.where_to_go.activities.DestinationDetailsActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.fragments.MapFragment;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.utilities.DatabaseUtils;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseObject;
import org.jetbrains.annotations.Contract;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.FilteredDestinationViewHolder> {
    private static final String TAG = "DestinationsAdapter";

    public Context context;

    private final List<Destination> destinations;
    private List<Marker> currentMarkers;
    private NavigationAdapter navigationListener;

    public DestinationsAdapter(Context _context, List<Destination> _destinations, List<Marker> _currentMarkers, NavigationAdapter _navigationListener) {
        context = _context;
        destinations = _destinations;
        currentMarkers = _currentMarkers;
        navigationListener = _navigationListener;
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
        Log.i(TAG, "onItemMove");

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

    public void onItemRemove(int position) {
        Log.i(TAG, "onItemDeleted");
        Log.i(TAG, "Previous size: " + destinations.size());
        Log.i(TAG, "Delete destination #" + position);

        Destination removeDestination = destinations.get(position);

        String removeDestinationId = removeDestination.getIdDB();
        ParseObject associatedTour = removeDestination.getParseObject(Destination.TOUR_ID);

        DatabaseUtils.removeDestinationsFromDatabaseIfExists(removeDestinationId);
        destinations.remove(position);
        notifyItemRemoved(position);

        Log.i(TAG, "Current size: " + destinations.size());

        if (getItemCount() == 0) {
            if (associatedTour != null) {
                String associatedTourId = Objects.requireNonNull(associatedTour).getObjectId();
                Log.i(TAG, "Remove tour: " + associatedTourId);
                DatabaseUtils.removeOneTourFromDatabaseIfExists(associatedTourId);
                Toast.makeText(context, "Go back Home...", Toast.LENGTH_SHORT).show();

                Object lock = new Object();
                synchronized (lock) {
                    try {
                        lock.wait(3000);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Can't wait. " + e.getMessage());
                    }
                }
                navigationListener.goHomeFragment();
            }
        }
    }

    public class FilteredDestinationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FilteredDestinationViewHolder";

        private final ImageView ivDestinationImage;
        private final TextView tvDestinationName;
        private final ImageButton ibRemove;

        public FilteredDestinationViewHolder(View itemView) {
            super(itemView);

            ivDestinationImage = itemView.findViewById(R.id.ivTourImage);
            tvDestinationName = itemView.findViewById(R.id.tvTourName);
            ibRemove = itemView.findViewById(R.id.ibRemove);

            ibRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemRemove(position);
                    MapFragment.resetMarkers(currentMarkers, position);
                }
            });
            
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
                Destination destination = destinations.get(position);
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

        @NonNull
        @Contract("_, _ -> param1")
        private void addInformationToIntent(@NonNull Intent _intent, @NonNull Destination destination) {
            // Add information to the intent
            _intent.putExtra("destination_photo", destination.getImageUrl());
            _intent.putExtra("destination_name", destination.getLocationName());
            _intent.putExtra("destination_phone", destination.getPhone());
            _intent.putExtra("destination_rating", destination.getRating());
            _intent.putExtra("destination_distance", destination.getDistance());
            _intent.putExtra("destination_address", destination.getAddress());
        }
    }

    public interface NavigationAdapter {
        void goHomeFragment();
    }
}
