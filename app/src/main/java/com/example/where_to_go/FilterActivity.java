package com.example.where_to_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.where_to_go.fragments.HomeFragment;
import com.example.where_to_go.fragments.MapFragment;
import com.example.where_to_go.utilities.SeekBarComparator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {

    private static final float HOURS_PER_DAY = 24;
    private static final String INTENT = "Filter";
    private static final String TAG = "FilterActivity";
    private static final int TOTAL_CATEGORIES = 8;
    private static final int TOTAL_PERCENTAGE = 100;
    private static int addCategoryClickCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        EditText tvNoDays = findViewById(R.id.tvCountDays);
        EditText tvPriceUnder = findViewById(R.id.tvPriceUnder);
        Spinner spTransportation = findViewById(R.id.spTransportation);
        SeekBar[] seekBars = new SeekBar[TOTAL_CATEGORIES];

        for (int id = 0; id < TOTAL_CATEGORIES; ++id) {
            String seekBarId = "seekBar" + id;
            seekBars[id] = findViewById(getResources().getIdentifier(seekBarId, "id", getPackageName()));
        }

        for (int numId = 0; numId < TOTAL_CATEGORIES; ++numId) {
            setCategoryInVisible(numId);
        }

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            try {
                receiveFilterResult(tvNoDays, tvPriceUnder, spTransportation, seekBars);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        Button btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.llFilter, new HomeFragment()).commit();
            finish();
        });

        Button btnAddCategory = findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(v -> {
            if (addCategoryClickCount >= TOTAL_CATEGORIES - 1) {
                Log.i(TAG, "Remove id: " + (addCategoryClickCount - 1));
                setCategoryInVisible(addCategoryClickCount);
                --addCategoryClickCount;
            }
            else {
                ++addCategoryClickCount;
                Log.i(TAG, "Add id: " + addCategoryClickCount);
                setCategoryVisible(addCategoryClickCount);
            }

            if (addCategoryClickCount == TOTAL_CATEGORIES - 1) {
                btnAddCategory.setText(R.string.remove_types);
            } else {
                btnAddCategory.setText(R.string.add_types_max_10);
            }

            resetInvisibleSeekBarValue(addCategoryClickCount);
        });

        for (SeekBar seekBar : seekBars) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    List<SeekBar> visibleSeekBars = getVisibleSeekBars(seekBars);
                    int currentTotalProgress = getCurrentTotalProgress(visibleSeekBars);
                    if (currentTotalProgress > TOTAL_PERCENTAGE) {
                        adjustSeekBarAccordingly(visibleSeekBars, currentTotalProgress);
                    }
                }
            });
        }
    }
    
    @NonNull
    private List<SeekBar> getVisibleSeekBars(@NonNull SeekBar[] seekBars) {
        List<SeekBar> visibleSeekBars = new ArrayList<>();
        for (SeekBar seekBar : seekBars) {
            if (seekBar.getProgress() > 0) {
                visibleSeekBars.add(seekBar);
            }
        }
        return visibleSeekBars;
    }

    private int getCurrentTotalProgress(@NonNull List<SeekBar> visibleSeekBars) {
        int currentTotalProgress = 0;
        for (SeekBar seekBar : visibleSeekBars) {
            currentTotalProgress += seekBar.getProgress();
        }
        return currentTotalProgress;
    }

    private void resetInvisibleSeekBarValue(int addCategoryClickCount) {
        String seekBarId = "seekBar" + addCategoryClickCount;
        SeekBar seekBar = findViewById(getResources().getIdentifier(seekBarId, "id", getPackageName()));
        seekBar.setProgress(0);
    }

    // HELPER METHODS

    private void adjustSeekBarAccordingly(@NonNull List<SeekBar> visibleSeekBars, int currentTotalProgress) {
        Log.i(TAG, "adjustSeekBarAccordingly");

        visibleSeekBars.sort(new SeekBarComparator());

        while (currentTotalProgress > TOTAL_PERCENTAGE) { // Remove a suitable amount from most-to-least-progress seekBar
            for (SeekBar visibleSeekBar : visibleSeekBars) {
                currentTotalProgress = Math.max(currentTotalProgress - TOTAL_PERCENTAGE, 0);
                int progressToSet = Math.max(visibleSeekBar.getProgress() - currentTotalProgress, 0);
                visibleSeekBar.setProgress(progressToSet);
            }
        }
    }

    private void setCategoryInVisible(int _addCategoryClickCount) {
        String llDestinationId = "llDestinationType" + _addCategoryClickCount;
        LinearLayout llDestinationType = findViewById(getResources().getIdentifier(llDestinationId, "id", getPackageName()));
        llDestinationType.setVisibility(View.INVISIBLE);
    }

    private void setCategoryVisible(int _addCategoryClickCount) {
        String llDestinationId = "llDestinationType" + _addCategoryClickCount;
        LinearLayout llDestinationType = findViewById(getResources().getIdentifier(llDestinationId, "id", getPackageName()));
        llDestinationType.setVisibility(View.VISIBLE);
    }

    private void receiveFilterResult(@NonNull EditText _tvNoDays, @NonNull EditText _tvPriceUnder, @NonNull Spinner _spTransportation, SeekBar[] _seekBars) throws JSONException {
        String tvNoDays_str = _tvNoDays.getText().toString();
        String tvPriceUnder_str = _tvPriceUnder.getText().toString();
        String spTransportationType = _spTransportation.getSelectedItem().toString();
        String category = getCategory(_seekBars);

        if (!tvNoDays_str.isEmpty() && !tvPriceUnder_str.isEmpty()) {
            Log.i(TAG, "Retrieved result: ");
            Log.i(TAG, "Number of days: " + tvNoDays_str);
            Log.i(TAG, "Price Under: " + tvPriceUnder_str);
            Log.i(TAG, "Destination Type: " + category);
            Log.i(TAG, "Transportation Type: " + spTransportationType);

            // Prepare transaction materials
            int noHours = (int) (Float.parseFloat(tvNoDays_str) * HOURS_PER_DAY);
            int priceUnder = Integer.parseInt(tvPriceUnder_str);
            JSONObject jsonFilterObject = getJsonFilterObject(noHours, priceUnder, category, spTransportationType);

            // Return to MapFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.i(TAG, "Begin to Map");
            fragmentManager.beginTransaction().replace(R.id.clFilter, new MapFragment(INTENT, jsonFilterObject)).commit();
        }
    }

    @NonNull
    private String getCategory(SeekBar[] _seekBars) {
        HashSet<String> category_set = new HashSet<>();
        List<String> categories = new ArrayList<>();
        StringBuilder returnCategory = new StringBuilder();

        for (int i = 0; i < _seekBars.length; ++i) {
            SeekBar currentSeekBar = _seekBars[i];

            if (currentSeekBar.getProgress() > 0) {
                // Get spinner
                String spDestinationId = "spDestinationType" + i;
                Spinner spDestination = findViewById(getResources().getIdentifier(spDestinationId, "id", getPackageName()));

                // Get category code one at a time
                Map<String, String> category_map = getBaseCategories(); // Key: Category Title, Value: Category Code

                String categoryTitle = spDestination.getSelectedItem().toString();
                String categoryCode = category_map.get(categoryTitle);

                if (categoryCode != null && !category_set.contains(categoryCode)) {
                    category_set.add(categoryCode);
                    categories.add(categoryCode);
                    categories.add(",");
                }
            }
        }

        for (int i = 0; i < categories.size() - 1; ++i) {
            returnCategory.append(categories.get(i));
        }

        if (returnCategory.length() == 0) {
            return "";
        }
        return returnCategory.toString();
    }

    @NonNull
    private JSONObject getJsonFilterObject(int noHours, int priceUnder, String category, String spTransportationType) throws JSONException {
        JSONObject jsonFilterObject = new JSONObject();

        jsonFilterObject.put("no_hours", noHours);
        jsonFilterObject.put("price_under", priceUnder);
        jsonFilterObject.put("destination_type", category);
        jsonFilterObject.put("transportation_option", spTransportationType);

        return jsonFilterObject;
    }

    @NonNull
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