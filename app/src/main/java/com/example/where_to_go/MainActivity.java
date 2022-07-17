package com.example.where_to_go;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        if (ParseUser.getCurrentUser() == null) {
            // If someone has logged out already
            Log.i(TAG, "goLogin");
            goLoginActivity();
        } else {
            Log.i(TAG, "goHome");
            goHomeActivity();
        }
    }

    private void goHomeActivity() {
        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
        finish();
    }

    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}