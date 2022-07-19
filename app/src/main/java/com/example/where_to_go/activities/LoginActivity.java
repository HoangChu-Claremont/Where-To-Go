package com.example.where_to_go.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.where_to_go.R;
import com.example.where_to_go.models.Friend;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    public static String username;
    private AccessTokenTracker accessTokenTracker;
    public static List<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Log.i(TAG, "isLoggedIn:" + isLoggedIn);

        if (isLoggedIn) {
            goNavigationActivity();
        }

        CallbackManager callbackManager = CallbackManager.Factory.create();
        friends = new ArrayList<>();

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

        btnFBLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                List<String> permissions = new ArrayList<>();
                permissions.add("email");
                permissions.add("public_profile");
                permissions.add("user_friends");
                btnFBLogin.setPermissions(permissions);

                addFacebookFriends();

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions,
                        (user, err) -> { // Succeeded
                            createNewUserOrLinkToFacebook(permissions, user, err);
                            goNavigationActivity();
                        });
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(@NonNull FacebookException e) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(@Nullable AccessToken oldAccessToken, @Nullable AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    friends.clear();
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    // HELPER METHODS
    private void addFacebookFriends() {
        friends.add(new Friend("1", "a"));
        friends.add(new Friend("2", "b"));
        Log.i(TAG, "Added " + friends.size() + " friends.");
    }

    private void login(String _username, String _password) {
        Log.i(TAG, "Logging in with username: " + _username);

        username = _username;
        // Navigate to MainActivity if successfully log in
        ParseUser.logInInBackground(_username, _password, (user, e) -> {
            // If fail
            if (e != null) {
                Log.e(TAG, "Issue with login", e);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // If logged in
            user.setUsername(_username);
            user.setPassword(_password);
            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();

            goNavigationActivity();
        });
    }

    public void signUp(String _username, String _password) {
        Log.i(TAG, "Signing up...");

        ParseUser user = new ParseUser();

        // Invoke signUpInBackground
        user.signUpInBackground(e -> {
            // Sign up failed.
            if (e != null) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // Sign up succeed.
            user.setUsername(_username);
            username = _username;
            user.setPassword(_password);
            Toast.makeText(LoginActivity.this, "Sign Up Succeed!", Toast.LENGTH_SHORT).show();

            goNavigationActivity();
        });
    }

    private void createNewUserOrLinkToFacebook(List<String> permissions, ParseUser user, ParseException err) {
        Log.i(TAG, "createNewUserOrLinkToFacebook");

        if (err != null) {
            Log.i(TAG, "Uh oh. Error occurred. " + err);
        } else if (user == null) {
            Log.i(TAG, "Uh oh. The user cancelled the Facebook login.");
        } else if (user.isNew()) {
            Log.i(TAG, "User signed up and logged in through Facebook!");
        } else {
            Log.i(TAG, "User logged in through Facebook!");
            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT)
                    .show();

            linkCurrentUserToFacebookLogin(permissions, user);
        }
    }

    private void linkCurrentUserToFacebookLogin(List<String> permissions, ParseUser user) {
        Log.i(TAG, "linkCurrentUserToFacebookLogin");

        if (!ParseFacebookUtils.isLinked(user)) {
            ParseFacebookUtils.linkWithReadPermissionsInBackground(user, LoginActivity.this, permissions, e -> {
                if (ParseFacebookUtils.isLinked(user)) {
                    Log.i(TAG, "User is linked in with Facebook!");
                    username = user.getUsername();
                } else {
                    Log.i(TAG, "User can't link with Facebook. " + e.getMessage());
                    username = ParseUser.getCurrentUser().getUsername();
                }
            });
        }
    }

    private void goNavigationActivity() {
        Log.i(TAG, "goNavigationActivity");

        Intent i = new Intent(this, NavigationActivity.class);
        startActivity(i);
        finish();
    }
}