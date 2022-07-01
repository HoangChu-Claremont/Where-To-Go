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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.where_to_go.DestinationDetailsActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Destination;

import org.parceler.Parcels;

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
        private ImageView ivTourImage;
        private TextView tvTourName;

        public FilteredDestinationViewHolder(View itemView) {
            super(itemView);

            ivTourImage = itemView.findViewById(R.id.ivTourImage);
            tvTourName = itemView.findViewById(R.id.tvTourName);
            
            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Destination filteredDestination) {
            tvTourName.setText(filteredDestination.getLocationName());
            Glide.with(context).load(filteredDestination.getImageUrl()).into(ivTourImage);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position, this won't work if the class is static
                Destination destination = destinations.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, DestinationDetailsActivity.class);
                intent.putExtra(Destination.class.getSimpleName(), Parcels.wrap(destination));
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}
