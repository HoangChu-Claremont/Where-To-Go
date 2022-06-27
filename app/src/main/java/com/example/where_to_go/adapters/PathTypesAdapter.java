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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.where_to_go.NavigationActivity;
import com.example.where_to_go.R;
import com.example.where_to_go.fragments.MapFragment;
import com.example.where_to_go.models.PathType;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Map;

public class PathTypesAdapter extends RecyclerView.Adapter<PathTypesAdapter.PathTypesViewHolder> {

    List<PathType> pathTypes;
    Context context;

    public PathTypesAdapter(Context _context, List<PathType> _pathTypes) {
        context = _context; // what does this equal?
        pathTypes = _pathTypes;
    }

    @NonNull
    @Override
    public PathTypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.item_top_path, parent, false);
        return new PathTypesViewHolder(topPathView);
    }

    @Override
    public void onBindViewHolder(@NonNull PathTypesViewHolder holder, int position) {
        PathType pathType = pathTypes.get(position);
        holder.bind(pathType);
    }

    @Override
    public int getItemCount() {
        return pathTypes.size();
    }

    public class PathTypesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG = "PathTypesViewHolder";
        private ImageView mPathImage;
        private TextView mPathName;

        public PathTypesViewHolder(@NonNull View itemView) {
            super(itemView);
            mPathImage = itemView.findViewById(R.id.ivPathImage);
            mPathName = itemView.findViewById(R.id.tvPathName);
            // add this as the itemView's OnClickListener
            itemView.setOnClickListener(this);
        }

        public void bind(PathType pathType) {
            mPathName.setText(pathType.getPathTypeName());
            Glide.with(context).load(pathType.getPathTypeImageUrl()).into(mPathImage);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the post at the position, this won't work if the class is static
                PathType pathType = pathTypes.get(position);

                // Switch HomeFragment -> MapFragment
                BottomNavigationView bottomNavigationView = ((NavigationActivity)context).bottomNavigationView;
                bottomNavigationView.setSelectedItemId(R.id.action_map);
            }
        }
    }
}
