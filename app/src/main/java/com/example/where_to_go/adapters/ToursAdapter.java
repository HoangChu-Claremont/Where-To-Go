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

public class ToursAdapter extends RecyclerView.Adapter<ToursAdapter.FeaturedTourViewHolder> {
    private static final String TAG = "ToursAdapter";
    private List<Tours> featuredTours;
    private Context context;

    public ToursAdapter(Context _context, List<Tours> _featured_tours) {
        context = _context;
        featuredTours = _featured_tours;
    }

    @NonNull
    @Override
    public FeaturedTourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topTourView = LayoutInflater.from(context).inflate(R.layout.collection, parent, false);
        return new FeaturedTourViewHolder(topTourView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedTourViewHolder holder, int position) {
        Tours featuredTour = featuredTours.get(position);
        holder.bind(featuredTour);
    }

    @Override
    public int getItemCount() {
        return featuredTours.size();
    }

    public class FeaturedTourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FeaturedTourViewHolder";
        private ImageView ivTourImage;
        private TextView ivTourName;
        private ImageButton ibTourBookmark;

        public FeaturedTourViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTourImage = itemView.findViewById(R.id.ivPathImage);
            ivTourName = itemView.findViewById(R.id.tvPathName);
            ibTourBookmark = itemView.findViewById(R.id.ibBookmark);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Tours featuredTour) {
            ivTourName.setText(featuredTour.getTourName());
            Glide.with(context).load("http://via.placeholder.com/300.png").into(ivTourImage);
            showSavedStatus(featuredTour.getSaved());

            ibTourBookmark.setOnClickListener(v -> {
                Log.i(TAG, String.valueOf(featuredTour.getSaved()));
                featuredTour.setIsSaved(!featuredTour.getSaved());
                Log.i(TAG, String.valueOf(featuredTour.getSaved()));

                featuredTour.saveInBackground();
                showSavedStatus(featuredTour.getSaved());
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
                ibTourBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark_filled));
            } else {
                ibTourBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark));
            }
        }
    }
}