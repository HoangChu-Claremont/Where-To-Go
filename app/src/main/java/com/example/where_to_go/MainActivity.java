package com.example.where_to_go;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (ParseUser.getCurrentUser() == null) {
            // If someone has logged out already
            goLoginActivity();
        } else {
            goHomeActivity();
        }
    }

    private void goHomeActivity() {
    }

    private void goLoginActivity() {
    }
}