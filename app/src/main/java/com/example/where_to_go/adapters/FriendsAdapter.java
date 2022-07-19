package com.example.where_to_go.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Friend;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private List<Friend> friends;
    private Context context;

    public FriendsAdapter(Context _context, List<Friend> _friends) {
        context = _context;
        friends = _friends;
    }

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View friendView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends, parent, false);
        return new FriendsViewHolder(friendView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsViewHolder holder, int position) {
        Picasso.get().load("https://graph.facebook.com/" + friends.get(position).getId() + "/picture?type=large")
                .into(holder.ivFriendImage);
        holder.tvFriendName.setText(friends.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFriendImage;
        TextView tvFriendName;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFriendImage = itemView.findViewById(R.id.ivFriendImage);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);
        }
    }
}
