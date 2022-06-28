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
import com.example.where_to_go.models.PathBundle;

import java.util.List;

public class FilteredPathAdapter extends RecyclerView.Adapter<FilteredPathAdapter.FilteredPathViewHolder> {
    private static final String TAG = "FilteredPathAdapter";
    public Context context;
    private List<Destination> destinations;

    public FilteredPathAdapter(Context _context, List<Destination> _destinations) {
        context = _context;
        destinations = _destinations;
    }

    @NonNull
    @Override
    public FilteredPathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.item_top_path, parent, false);
        return new FilteredPathViewHolder(topPathView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilteredPathViewHolder holder, int position) {
        Destination destination = destinations.get(position);
        holder.bind(destination);
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public class FilteredPathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FeaturedPathViewHolder";
        private ImageView ivPathImage;
        private TextView tvPathName;

        public FilteredPathViewHolder(View itemView) {
            super(itemView);
            ivPathImage = itemView.findViewById(R.id.ivPathImage);
            tvPathName = itemView.findViewById(R.id.tvPathName);
            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Destination filteredPath) {
            tvPathName.setText(filteredPath.getTitle());
            Glide.with(context).load(filteredPath.getImageUrl()).into(ivPathImage);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
