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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.where_to_go.activities.NavigationActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Tour;
import com.example.where_to_go.utilities.DatabaseUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ToursAdapter extends RecyclerView.Adapter<ToursAdapter.FeaturedTourViewHolder> {
    private static final String TAG = "ToursAdapter";
    public static int POSITION = -1;
    private List<Tour> inputTours;
    private Context context;

    public ToursAdapter(Context _context, List<Tour> _featured_tours) {
        context = _context;
        inputTours = _featured_tours;
    }

    @NonNull
    @Override
    public FeaturedTourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topTourView = LayoutInflater.from(context).inflate(R.layout.collection, parent, false);
        return new FeaturedTourViewHolder(topTourView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedTourViewHolder holder, int position) {
        Tour featuredTour = inputTours.get(position);
        holder.bind(featuredTour);
    }

    @Override
    public int getItemCount() {
        return inputTours.size();
    }

    public class FeaturedTourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "FeaturedTourViewHolder";
        private ImageView ivTourImage;
        private TextView ivTourName;
        private ImageButton ibTourBookmark;
        private ImageButton ibRemove;

        public FeaturedTourViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTourImage = itemView.findViewById(R.id.ivTourImage);
            ivTourName = itemView.findViewById(R.id.tvTourName);
            ibTourBookmark = itemView.findViewById(R.id.ibBookmark);
            ibRemove = itemView.findViewById(R.id.ibRemove);

            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull Tour tour) {
            Log.i(TAG, "binding");

            Fragment currentVisibleFragmentIsProfile = ((AppCompatActivity) context).getSupportFragmentManager()
                    .findFragmentByTag("ProfileFragment");

            if (isProfileFragment(currentVisibleFragmentIsProfile)) { // Special layout in ProfileFragment
                ibTourBookmark.setVisibility(View.INVISIBLE);
            }

            ivTourName.setText(tour.getTourNameDB());

            Glide.with(context).load("https://imgur.com/a/K0wRQZO")
                    .centerCrop()
                    .placeholder(R.drawable.profile_gradient)
                    .into(ivTourImage); // TODO: Set this image right
            showSavedStatus(tour.getIsSavedDB());

            ibTourBookmark.setOnClickListener(v -> {
                tour.setIsSavedDB(!tour.getIsSavedDB());
                tour.saveInBackground();
                showSavedStatus(tour.getIsSavedDB());
            });

            ibRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    String removeTourID = tour.getObjectId();
                    inputTours.remove(position);
                    notifyItemRemoved(position);

                    // Just switch state of a saved tour
                    if (isProfileFragment(currentVisibleFragmentIsProfile)) {
                        tour.setIsSavedDB(!tour.getIsSavedDB());
                        tour.saveInBackground();
                    } else {
                        DatabaseUtils.removeOneTourFromDatabaseIfExists(removeTourID);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: " + v);

            // gets item position
            int position = getAdapterPosition();

            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                POSITION = position;
                // Switch HomeFragment -> MapFragment
                BottomNavigationView bottomNavigationView = ((NavigationActivity) context).bottomNavigationView;
                bottomNavigationView.setSelectedItemId(R.id.action_map);
            }
        }

        // HELPER METHODS
        private void showSavedStatus(boolean isSaved) {
            Log.i(TAG, "showSavedStatus");

            if (isSaved) {
                ibTourBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark_filled));
            } else {
                ibTourBookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmark));
            }
        }
    }

    private boolean isProfileFragment(Fragment currentFragment) {
        return currentFragment != null && currentFragment.isVisible();
    }
}
