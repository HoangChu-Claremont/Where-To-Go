package com.example.where_to_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.where_to_go.fragments.HomeFragment;
import com.example.where_to_go.fragments.MapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class FilterActivity extends AppCompatActivity {

    private static final float HOURS_PER_DAY = 24;
    private static final String INTENT = "Filter";
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

        btnSubmit.setOnClickListener(v -> receiveFilterResult(tvNoDays, tvPriceUnder, spDestinationType, spTransportation));

        btnReturn.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.llFilter, new HomeFragment()).commit();
            finish();
        });
    }

    // HELPER METHODS

    private void receiveFilterResult(@NonNull EditText _tvNoDays, @NonNull EditText _tvPriceUnder, @NonNull Spinner _spDestinationType, @NonNull Spinner _spTransportation) {
        String tvNoDays_str = _tvNoDays.getText().toString();
        String tvPriceUnder_str = _tvPriceUnder.getText().toString();
        String spTransportationType = _spTransportation.getSelectedItem().toString();

        String spDestinationType = _spDestinationType.getSelectedItem().toString(); // TODO: Should be made from a list

        Map<String, String> categories = null;

        categories.put("Food", "food");
        categories.put("Nightlife", "nightlife");
        categories.put("Restaurants", "restaurants");
        categories.put("Shopping", "shopping");
        categories.put("Religious", "religiousorgs");
        categories.put("Landmarks", "landmarks");
        categories.put("Hotels", "hotelstravel");
        categories.put("Natural", "active");
        categories.put("Arts & Entertainment", "arts");
        categories.put("Beauty & Spas", "beautysvc");
        categories.put("Public Event", "eventservices");
        categories.put("Financial Services", "financialservices");
        categories.put("Healthcare", "health");

        if (!tvNoDays_str.isEmpty() && !tvPriceUnder_str.isEmpty() && !spDestinationType.isEmpty()) {
            Log.i(TAG, "Retrieved result: ");
            Log.i(TAG, "Number of days: " + tvNoDays_str);
            Log.i(TAG, "Price Under: " + tvPriceUnder_str);
            Log.i(TAG, "Destination Type: " + spDestinationType);
            Log.i(TAG, "Transportation Type: " + spTransportationType);

            int noHours = (int) (Float.parseFloat(tvNoDays_str) * HOURS_PER_DAY);
            int priceUnder = Integer.parseInt(tvPriceUnder_str);
            String category = categories.get(spDestinationType);

            JSONObject jsonResult = new JSONObject();
            try {
                jsonResult.put("no_hours", noHours);
                jsonResult.put("price_under", priceUnder);
                jsonResult.put("destination_type", category);
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