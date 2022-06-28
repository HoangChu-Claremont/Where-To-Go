package com.example.where_to_go.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Destination;

import java.util.List;

public class FilteredDestinationAdapter extends RecyclerView.Adapter<FilteredDestinationAdapter.FilteredDestinationViewHolder> {
    private static final String TAG = "FilteredDestinationAdapter";
    public Context context;
    private List<Destination> destinations;

    public FilteredDestinationAdapter(Context _context, List<Destination> _destinations) {
        context = _context;
        destinations = _destinations;
    }

    @NonNull
    @Override
    public FilteredDestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.item_top_path, parent, false);
        return new FilteredDestinationViewHolder(topPathView);
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

        public void bind(@NonNull Destination filteredDestination) {
            tvPathName.setText(filteredDestination.getTitle());
            Glide.with(context).load(filteredDestination.getImageUrl()).into(ivPathImage);
        }

        @Override
        public void onClick(View v) {

        }
    }
}