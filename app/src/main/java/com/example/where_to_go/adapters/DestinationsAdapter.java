package com.example.where_to_go.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Destinations;

import java.util.Collections;
import java.util.List;

public class DestinationsAdapter extends RecyclerView.Adapter<DestinationsAdapter.FilteredDestinationViewHolder> {
    private static final String TAG = "DestinationsAdapter";
    public Context context;
    private final List<Destinations> destinations;

    public DestinationsAdapter(Context _context, List<Destinations> _destinations) {
        context = _context;
        destinations = _destinations;
    }

    @NonNull
    @Override
    public FilteredDestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.collection, parent, false);
        return new FilteredDestinationViewHolder(topPathView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilteredDestinationViewHolder holder, int position) {
        Destinations destination = destinations.get(position);
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
        private static final String TAG = "FeaturedPathViewHolder";
        private ImageView ivPathImage;
        private TextView tvPathName;

        public FilteredDestinationViewHolder(View itemView) {
            super(itemView);
            ivPathImage = itemView.findViewById(R.id.ivPathImage);
            tvPathName = itemView.findViewById(R.id.tvPathName);
            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Destinations filteredDestination) {
            tvPathName.setText(filteredDestination.getTitle());
            Glide.with(context).load(filteredDestination.getImageUrl()).into(ivPathImage);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
