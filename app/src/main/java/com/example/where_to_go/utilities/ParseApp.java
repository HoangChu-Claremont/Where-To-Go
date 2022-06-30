package com.example.where_to_go.utilities;

import android.app.Application;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Destinations;
import com.example.where_to_go.models.Tours;
import com.example.where_to_go.models.Users;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Users.class);
        ParseObject.registerSubclass(Tours.class);
        ParseObject.registerSubclass(Destinations.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
    }
}
