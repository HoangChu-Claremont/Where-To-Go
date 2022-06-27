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
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Path;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.util.List;

public class TopPathsAdapter extends RecyclerView.Adapter<TopPathsAdapter.TopPathsViewHolder> {
    private static final String TAG = "TopPathsAdapter";
    public Context context;
    private List<Path> paths;

    public TopPathsAdapter(Context _context, List<Path> _paths) {
        context = _context;
        paths = _paths;
    }

    @NonNull
    @Override
    public TopPathsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View topPathView = LayoutInflater.from(context).inflate(R.layout.item_top_path, parent, false);
        return new TopPathsViewHolder(topPathView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPathsAdapter.TopPathsViewHolder holder, int position) {
        Path path = paths.get(position);
        holder.bind(path);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class TopPathsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPathImage;
        private TextView mPathName;

        public TopPathsViewHolder(View itemView) {
            super(itemView);
            mPathImage = itemView.findViewById(R.id.ivPathImage);
            mPathName = itemView.findViewById(R.id.tvPathName);
        }

        public void bind(@NonNull Path path) {
            // Set TextView
            mPathName.setText(path.TOP_RATED);
            Glide.with(context).load("http://via.placeholder.com/300.png").into(mPathImage);

            // Set ImageView
//            RequestOptions requestOptions = new RequestOptions();
//            requestOptions = requestOptions.transforms(new CenterCrop()).format(DecodeFormat.PREFER_ARGB_8888).override(150, 110);
//            ParseFile pathImage = path.getPathImage();

//            if (pathImage != null) {
//                Glide.with(context)
//                        .load(pathImage.getUrl())
//                        .apply(requestOptions)
//                        .into(mPathImage);
//            } else {
//                mPathImage.setImageResource(R.drawable.ic_path_placeholder);
//            }

//            path.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    Log.d(TAG, "done");
//                }
//            });
        }
    }
}
