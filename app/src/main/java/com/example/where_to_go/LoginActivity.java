package com.example.where_to_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set Values
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // Button clicks
        btnLogin.setOnClickListener(v -> {
            Log.i(TAG, "onClick Login Button");

            // Get user input
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Log in -> MainActivity
            loginUser(username, password);
        });

        btnSignup.setOnClickListener((View.OnClickListener) v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            // Log in -> LoginActivity
            signUp(username, password);
        });

    }

    // HELPER METHODS

    private void loginUser(String username, String password) {
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
            goHomeActivity();
            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
        });
    }

    public void signUp(String username, String password) {
        ParseUser user =  new ParseUser();

        // Set core properties
        user.setUsername(username);
        user.setPassword(password);

        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            // Sign up failed.
            if (e != null) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            goHomeActivity();
            Toast.makeText(LoginActivity.this, "Sign Up Succeed!", Toast.LENGTH_SHORT).show();
        });
    }

    private void goHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}