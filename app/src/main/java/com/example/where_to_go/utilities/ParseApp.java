package com.example.where_to_go.utilities;
import android.app.Application;

import com.example.where_to_go.R;
import com.example.where_to_go.models.DestinationCollections;
import com.parse.Parse;
import com.parse.ParseObject;
import com.yelp.clientlib.entities.User;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(DestinationCollections.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );
    }
}
