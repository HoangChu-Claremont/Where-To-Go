package com.example.where_to_go.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.where_to_go.R;
import com.example.where_to_go.adapters.FilteredPathAdapter;
import com.example.where_to_go.models.Destination;
import com.example.where_to_go.utilities.FilterAlgorithm;
import com.example.where_to_go.utilities.YelpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends Fragment {

    private FilteredPathAdapter filteredPathAdapter;
    private List<Destination> filteredPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(), "You're in Map!", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        CardView cvContinuePath = view.findViewById(R.id.cvContinuePath);
//
//        // TODO: Recommendation Algorithm
//        cvContinuePath.setOnClickListener(v -> {
//
//        });

        // Featured Destination
//        getFilteredPath();
//        setFilteredPathRecyclerView();
    }

    // HELPER METHODS

    private void getFilteredPath() {

        // query for top 10 paths based on average
        filteredPath = new ArrayList<>();
        final YelpClient topPath = new YelpClient();

        topPath.getResponse(-122.1483654685629, 37.484668049999996, 3, new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonData = new JSONObject(responseData);

                    JSONArray jsonResults = jsonData.getJSONArray("businesses");
                    filteredPath.addAll(FilterAlgorithm.getTopRatedPath(jsonResults));

                    // Avoid the "Only the original thread that created a view hierarchy
                    // can touch its views adapter" error
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //change View Data
                            filteredPathAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setFilteredPathRecyclerView() {
        RecyclerView rvTopPaths = getView().findViewById(R.id.rvTopPaths); // TODO: Need adjustment (variable name, resource id, etc.) here

        // Create the Adapter
        filteredPathAdapter = new FilteredPathAdapter(getContext(), filteredPath);

        // Set Layout Manager
        LinearLayoutManager tLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvTopPaths.setLayoutManager(tLayoutManager);
        rvTopPaths.setHasFixedSize(true); // always get top 10 paths

        // Set the Adapter on RecyclerView
        rvTopPaths.setAdapter(filteredPathAdapter);
    }
}