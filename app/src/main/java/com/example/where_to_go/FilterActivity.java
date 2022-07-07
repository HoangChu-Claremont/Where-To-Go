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

import java.util.HashMap;
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

        btnSubmit.setOnClickListener(v -> {
            try {
                receiveFilterResult(tvNoDays, tvPriceUnder, spDestinationType, spTransportation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        btnReturn.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.llFilter, new HomeFragment()).commit();
            finish();
        });
    }

    // HELPER METHODS

    private void receiveFilterResult(@NonNull EditText _tvNoDays, @NonNull EditText _tvPriceUnder, @NonNull Spinner _spDestinationType, @NonNull Spinner _spTransportation) throws JSONException {
        String tvNoDays_str = _tvNoDays.getText().toString();
        String tvPriceUnder_str = _tvPriceUnder.getText().toString();
        String spTransportationType = _spTransportation.getSelectedItem().toString();

        String spDestinationType = _spDestinationType.getSelectedItem().toString(); // TODO: Should be made from a list

        Map<String, String> categories = getBaseCategories();

        if (!tvNoDays_str.isEmpty() && !tvPriceUnder_str.isEmpty() && !spDestinationType.isEmpty()) {
            Log.i(TAG, "Retrieved result: ");
            Log.i(TAG, "Number of days: " + tvNoDays_str);
            Log.i(TAG, "Price Under: " + tvPriceUnder_str);
            Log.i(TAG, "Destination Type: " + spDestinationType);
            Log.i(TAG, "Transportation Type: " + spTransportationType);

            int noHours = (int) (Float.parseFloat(tvNoDays_str) * HOURS_PER_DAY);
            int priceUnder = Integer.parseInt(tvPriceUnder_str);
            String category = categories.get(spDestinationType);

            JSONObject jsonFilterObject = getJsonFilterObject(noHours, priceUnder, category, spTransportationType);

            // Return to MapFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.i(TAG, "Begin to Map");
            fragmentManager.beginTransaction().replace(R.id.clFilter, new MapFragment(INTENT, jsonFilterObject)).commit();
        }
    }

    private JSONObject getJsonFilterObject(int noHours, int priceUnder, String category, String spTransportationType) throws JSONException {
        JSONObject jsonFilterObject = new JSONObject();

        jsonFilterObject.put("no_hours", noHours);
        jsonFilterObject.put("price_under", priceUnder);
        jsonFilterObject.put("destination_type", category);
        jsonFilterObject.put("transportation_option", spTransportationType);

        return jsonFilterObject;
    }

    private Map<String, String> getBaseCategories() {
        Map<String, String> _categories = new HashMap<>();

        _categories.put("Food", "food");
        _categories.put("Nightlife", "nightlife");
        _categories.put("Restaurants", "restaurants");
        _categories.put("Shopping", "shopping");
        _categories.put("Religious", "religiousorgs");
        _categories.put("Landmarks", "landmarks");
        _categories.put("Hotels", "hotelstravel");
        _categories.put("Natural", "active");
        _categories.put("Arts & Entertainment", "arts");
        _categories.put("Beauty & Spas", "beautysvc");
        _categories.put("Public Event", "eventservices");
        _categories.put("Financial Services", "financialservices");
        _categories.put("Healthcare", "health");

        return _categories;
    }
}