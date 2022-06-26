package com.example.where_to_go;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
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