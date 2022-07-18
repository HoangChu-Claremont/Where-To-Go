package com.example.where_to_go.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.where_to_go.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Log.i(TAG, "isLoggedIn:" + isLoggedIn);
        callbackManager = CallbackManager.Factory.create();

        // Set Values
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);
        LoginButton btnFBLogin = findViewById(R.id.fb_login_button);

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

        List<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        btnFBLogin.setPermissions(permissions);

        btnFBLogin.setOnClickListener(v -> {
            if (isLoggedIn) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, permissions);
            } else {
                btnFBLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        goNavigationActivity();
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(@NonNull FacebookException e) {

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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