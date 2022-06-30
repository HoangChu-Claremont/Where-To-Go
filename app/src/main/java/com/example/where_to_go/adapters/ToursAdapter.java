package com.example.where_to_go.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.where_to_go.NavigationActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Tours;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ToursAdapter extends RecyclerView.Adapter<ToursAdapter.FeaturedPathViewHolder> {
    private static final String TAG = "ToursAdapter";
    private List<Tours> featuredPaths;
    private Context context;

    public ToursAdapter(Context _context, List<Tours> _featured_paths) {
        context = _context;
        featuredPaths = _featured_paths;
    }

    @NonNull
    @Override
    public FeaturedPathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.collection, parent, false);
        return new FeaturedPathViewHolder(topPathView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedPathViewHolder holder, int position) {
        Tours featuredPath = featuredPaths.get(position);
        holder.bind(featuredPath);
    }

    @Override
    public int getItemCount() {
        return featuredPaths.size();
    }

    public class FeaturedPathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FeaturedPathViewHolder";
        private ImageView ivPathImage;
        private TextView ivPathName;
        private ImageButton ibPathBookmark;

        public FeaturedPathViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPathImage = itemView.findViewById(R.id.ivPathImage);
            ivPathName = itemView.findViewById(R.id.tvPathName);
            ibPathBookmark = itemView.findViewById(R.id.ibBookmark);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Tours featuredPath) {
            ivPathName.setText(featuredPath.getTourName());
            Glide.with(context).load("http://via.placeholder.com/300.png").into(ivPathImage);
            showSavedStatus(featuredPath.getSaved());

            ibPathBookmark.setOnClickListener(v -> {
                Log.i(TAG, String.valueOf(featuredPath.getSaved()));
                featuredPath.setIsSaved(!featuredPath.getSaved());
                Log.i(TAG, String.valueOf(featuredPath.getSaved()));

                featuredPath.saveInBackground();
                showSavedStatus(featuredPath.getSaved());
            });
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {

                // Switch HomeFragment -> MapFragment
                BottomNavigationView bottomNavigationView = ((NavigationActivity) context).bottomNavigationView;
                bottomNavigationView.setSelectedItemId(R.id.action_map);
            }
        }

        // HELPER METHODS
        private void showSavedStatus(boolean isSaved) {
            if (isSaved) {
                ibPathBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark_filled));
            } else {
                ibPathBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark));
            }
        }
    }
}
