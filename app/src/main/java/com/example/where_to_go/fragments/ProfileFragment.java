package com.example.where_to_go.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.where_to_go.R;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView tvAccountName;
    private TextView tvAccountTwitterName;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getContext(), "You're in Profile!", Toast.LENGTH_SHORT).show();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAccountName = view.findViewById(R.id.account_name);
        tvAccountTwitterName = view.findViewById(R.id.account_twitter_name);

        ParseUser currentUser = ParseUser.getCurrentUser();

        String accountName = currentUser.getUsername();
        String accountTwitterName = "@" + accountName;

        tvAccountName.setText(accountName);
        tvAccountTwitterName.setText(accountTwitterName);
    }
}