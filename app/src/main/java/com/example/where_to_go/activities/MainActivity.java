package com.example.where_to_go.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.where_to_go.R;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    public static double CURRENT_LONGITUDE = 0, CURRENT_LATITUDE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigateToFragment();
    }

    // HELPER METHODS

    private void navigateToFragment() {
        Log.i(TAG, "navigateToFragment");
        
        if (ParseUser.getCurrentUser() == null) { // If someone has logged out already
            goLoginActivity();
        } else {
            goHomeFragment();
        }
    }

    private void goHomeFragment() {
        Log.i(TAG, "goHomeFragment");
        
        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void goLoginActivity() {
        Log.i(TAG, "goLoginActivity");
        
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}