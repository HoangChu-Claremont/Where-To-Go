package com.example.where_to_go;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.where_to_go.fragments.HomeFragment;
import com.example.where_to_go.fragments.MapFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class FilterActivity extends AppCompatActivity {


    private static final float HOURS_PER_DAY = 24;
    private static final String INTENT = "filter";
    private static final String TAG = "FilterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        EditText tvNoDays = findViewById(R.id.tvCountDays);
        EditText tvPriceUnder = findViewById(R.id.tvPriceUnder);
        Spinner spDestinationType = findViewById(R.id.spDestinationType);
        Spinner spTransportation = findViewById(R.id.spTransportation);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnReturn = findViewById(R.id.btnReturn);

        btnSubmit.setOnClickListener(v -> {
            receiveFilterResult(tvNoDays, tvPriceUnder, spDestinationType, spTransportation);
        });

        btnReturn.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.llFilter, new HomeFragment()).commit();
            finish();
        });
    }

    // HELPER METHODS

    private void receiveFilterResult(EditText _tvNoDays, EditText _tvPriceUnder, Spinner _spDestinationType, Spinner _spTransportation) {
        String tvNoDays_str = _tvNoDays.getText().toString();
        String tvPriceUnder_str = _tvPriceUnder.getText().toString();
        String spDestinationType = _spDestinationType.getSelectedItem().toString();
        String spTransportationType = _spTransportation.getSelectedItem().toString();

        if (!tvNoDays_str.isEmpty() && !tvPriceUnder_str.isEmpty() && !spDestinationType.isEmpty()) {
            Log.i(TAG, "Retrieved result: ");
            Log.i(TAG, "Number of days: " + tvNoDays_str);
            Log.i(TAG, "Price Under: " + tvPriceUnder_str);
            Log.i(TAG, "Destination Type: " + spDestinationType);
            Log.i(TAG, "Transportation Type: " + spTransportationType);

            int noHours = (int) (Float.parseFloat(tvNoDays_str) * HOURS_PER_DAY);
            int priceUnder = Integer.parseInt(tvPriceUnder_str);

            JSONObject jsonResult = new JSONObject();
            try {
                jsonResult.put("no_hours", noHours);
                jsonResult.put("price_under", priceUnder);
                jsonResult.put("destination_type", spDestinationType);
                jsonResult.put("transportation_option", spTransportationType);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.i(TAG, "Begin to Map");
            fragmentManager.beginTransaction().replace(R.id.llFilter, new MapFragment(INTENT, jsonResult)).commit();
        }
    }
}