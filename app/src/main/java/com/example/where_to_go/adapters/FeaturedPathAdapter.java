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
import com.example.where_to_go.NavigationActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.models.FeaturedPath;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class FeaturedPathAdapter extends RecyclerView.Adapter<FeaturedPathAdapter.PathTypesViewHolder> {

    List<FeaturedPath> featuredPaths;
    Context context;

    public FeaturedPathAdapter(Context _context, List<FeaturedPath> _featured_paths) {
        context = _context; // what does this equal?
        featuredPaths = _featured_paths;
    }

    @NonNull
    @Override
    public PathTypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.item_top_path, parent, false);
        return new PathTypesViewHolder(topPathView);
    }

    @Override
    public void onBindViewHolder(@NonNull PathTypesViewHolder holder, int position) {
        FeaturedPath featuredPath = featuredPaths.get(position);
        holder.bind(featuredPath);
    }

    @Override
    public int getItemCount() {
        return featuredPaths.size();
    }

    public class PathTypesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG = "PathTypesViewHolder";
        private ImageView ivPathImage;
        private TextView ivPathName;

        public PathTypesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPathImage = itemView.findViewById(R.id.ivPathImage);
            ivPathName = itemView.findViewById(R.id.tvPathName);
            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull FeaturedPath featuredPath) {
            ivPathName.setText(featuredPath.getFeaturedPathName());
            Glide.with(context).load(featuredPath.getFeaturedPathImageUrl()).into(ivPathImage);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position, this won't work if the class is static
                FeaturedPath featuredPath = featuredPaths.get(position);

                // Switch HomeFragment -> MapFragment
                BottomNavigationView bottomNavigationView = ((NavigationActivity)context).bottomNavigationView;
                bottomNavigationView.setSelectedItemId(R.id.action_map);
            }
        }
    }
}
