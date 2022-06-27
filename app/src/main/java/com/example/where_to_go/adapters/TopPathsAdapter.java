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

public class TopPathsAdapter extends RecyclerView.Adapter<TopPathsAdapter.TopPathsViewHolder> {
    private static final String TAG = "TopPathsAdapter";
    public Context context;
    private List<Destination> destinations;

    public TopPathsAdapter(Context _context, List<Destination> _destinations) {
        context = _context;
        destinations = _destinations;
    }

    @NonNull
    @Override
    public TopPathsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.item_top_path, parent, false);
        return new TopPathsViewHolder(topPathView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPathsAdapter.TopPathsViewHolder holder, int position) {
        Destination destination = destinations.get(position);
        holder.bind(destination);
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public class TopPathsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPathImage;
        private TextView tvPathName;

        public TopPathsViewHolder(View itemView) {
            super(itemView);
            ivPathImage = itemView.findViewById(R.id.ivPathImage);
            tvPathName = itemView.findViewById(R.id.tvPathName);
        }

        public void bind(@NonNull Destination destination) {
            // Set TextView
//            mPathName.setText(destination.TOP_RATED);
            Glide.with(context).load("http://via.placeholder.com/300.png").into(ivPathImage);

            // Set ImageView
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions = requestOptions.transforms(new CenterCrop()).format(DecodeFormat.PREFER_ARGB_8888).override(150, 110);
//            ParseFile pathImage = destination.getPathImage();

//            if (pathImage != null) {
//                Glide.with(context)
//                        .load(pathImage.getUrl())
//                        .apply(requestOptions)
//                        .into(mPathImage);
//            } else {
//                mPathImage.setImageResource(R.drawable.ic_path_placeholder);
//            }

//            destination.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    Log.d(TAG, "done");
//                }
//            });
        }
    }
}
