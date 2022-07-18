package com.example.where_to_go.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.where_to_go.R;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set Values
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);
        Button facebookLoginButton = findViewById(R.id.fb_login_button);

        // Button clicks
        btnLogin.setOnClickListener(v -> {
            Log.i(TAG, "onClick Login Button");

            // Get user input
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Log in -> MainActivity
            login(username, password);
        });

        btnSignup.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Log in -> LoginActivity
            signUp(username, password);
        });
    }

    // HELPER METHODS

    private void login(String username, String password) {
        Log.i(TAG, "Logging in with username: " + username);

        // Navigate to MainActivity if successfully log in
        ParseUser.logInInBackground(username, password, (user, e) -> {
            // If fail
            if (e != null) {
                Log.e(TAG, "Issue with login", e);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // If logged in
            user.setUsername(username);
            user.setPassword(password);
            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();

            goNavigationActivity();
        });
    }

    public void signUp(String username, String password) {
        Log.i(TAG, "signing up...");
        ParseUser user = new ParseUser();

        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            // Sign up failed.
            if (e != null) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Sign up succeed.
            user.setUsername(username);
            user.setPassword(password);
            Toast.makeText(LoginActivity.this, "Sign Up Succeed!", Toast.LENGTH_SHORT).show();

            goNavigationActivity();
        });
    }

    private void goNavigationActivity() {
        Log.i(TAG, "goNavigationActivity");

        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
        finish();
    }
}