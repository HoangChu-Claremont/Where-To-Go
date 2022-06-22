package com.example.where_to_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;
    private LoginButton facebookLoginButton; // Log in by Facebook

    private CallbackManager callbackManager;
    private boolean isLoggedInFB = false;
    private String id, firstName, lastName, email;
    private URL profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set Values
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        facebookLoginButton = findViewById(R.id.login_button);

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

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d("fb", "request");

                //  Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                //  String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("response", response.toString());
                        isLoggedInFB = true;
                        getData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
                Log.d("fb", "request user log in now");
//                checkUser(ParseUser.getCurrentUser(), true);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
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

    private void getData(JSONObject object) {
        try{
            profilePic = new URL("https://graph.facebook.com/"+object.getString("id")+"/picture?width=500&height=500");
            firstName = object.getString("first_name");
            lastName = object.getString("last_name");
            email = object.getString("email");

        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void goHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}