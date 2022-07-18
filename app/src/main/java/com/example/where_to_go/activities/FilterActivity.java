package com.example.where_to_go.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.where_to_go.R;
import com.example.where_to_go.fragments.MapFragment;
import com.example.where_to_go.utilities.SeekBarComparator;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "FilterActivity";

    private static final String INTENT = "Filter";
    private static final int TOTAL_CATEGORIES = 8;
    private static final int TOTAL_PERCENTAGE = 100;

    private static int addCategoryClickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        EditText tvNoDays = findViewById(R.id.tvCountDays);
        Spinner spTransportation = findViewById(R.id.spTransportation);
        Spinner spPrice = findViewById(R.id.spPrice);
        SeekBar[] seekBars = new SeekBar[TOTAL_CATEGORIES];

        initCategories(seekBars);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> {
            try {
                receiveFilterResult(tvNoDays, spPrice, spTransportation, seekBars);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        });

        Button btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
            finish();
        });

        Button btnAddCategory = findViewById(R.id.btnAddCategory);
        btnAddCategory.setOnClickListener(v -> {

            Log.i(TAG, String.valueOf(addCategoryClickCount));
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

    // HELPER METHODS

    private void initCategories(SeekBar[] _seekBars) {
        Log.i(TAG, "initCategories");

        addCategoryClickCount = -1; // When click "Add Categories", 1st category is 0th-indexed.

        for (int id = 0; id < TOTAL_CATEGORIES; ++id) { // Add all currently existing seekbars
            String seekBarId = "seekBar" + id;
            _seekBars[id] = findViewById(getResources().getIdentifier(seekBarId, "id", getPackageName()));
            resetInvisibleSeekBarValue(id);
        }

        for (int id = 0; id < TOTAL_CATEGORIES; ++id) { // Init all of them invisible
            setCategoryInVisible(id);
        }
    }

    @NonNull
    private List<SeekBar> getVisibleSeekBars(@NonNull SeekBar[] seekBars) {
        Log.i(TAG, "getVisibleSeekBars");

        List<SeekBar> visibleSeekBars = new ArrayList<>();

        for (SeekBar seekBar : seekBars) {
            if (seekBar.getProgress() > 0) {
                visibleSeekBars.add(seekBar);
            }
        }

        return visibleSeekBars;
    }

    private int getCurrentTotalProgress(@NonNull List<SeekBar> visibleSeekBars) {
        Log.i(TAG, "getCurrentTotalProgress");

        int currentTotalProgress = 0;

        for (SeekBar seekBar : visibleSeekBars) {
            currentTotalProgress += seekBar.getProgress();
        }

        return currentTotalProgress;
    }

    private void resetInvisibleSeekBarValue(int addCategoryClickCount) {
        Log.i(TAG, "resetInvisibleSeekBarValue");

        String seekBarId = "seekBar" + addCategoryClickCount;
        SeekBar seekBar = findViewById(getResources().getIdentifier(seekBarId, "id", getPackageName()));

        if (addCategoryClickCount == 0) {
            seekBar.setProgress(100);
        } else {
            seekBar.setProgress(0);
        }
    }

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
        Log.i(TAG, "setCategoryInVisible");

        String llDestinationId = "llDestinationType" + _addCategoryClickCount;
        LinearLayout llDestinationType = findViewById(getResources().getIdentifier(llDestinationId, "id", getPackageName()));

        llDestinationType.setVisibility(View.INVISIBLE);
    }

    private void setCategoryVisible(int _addCategoryClickCount) {
        Log.i(TAG, "setCategoryVisible");

        String llDestinationId = "llDestinationType" + _addCategoryClickCount;
        LinearLayout llDestinationType = findViewById(getResources().getIdentifier(llDestinationId, "id", getPackageName()));

        llDestinationType.setVisibility(View.VISIBLE);
    }

    private synchronized void receiveFilterResult(@NonNull EditText _tvNoDays, @NonNull Spinner _spPrice,
                                                  @NonNull Spinner _spTransportation, SeekBar[] _seekBars) throws JSONException {
        Log.i(TAG, "receiveFilterResult");

        String tvNoDays_str = _tvNoDays.getText().toString();
        String spPrice = _spPrice.getSelectedItem().toString();
        String spTransportationType = _spTransportation.getSelectedItem().toString();
        String category = getCategory(_seekBars);
        List<Integer> preferences = getPreferences(_seekBars);

        if (tvNoDays_str.isEmpty()) {
            Toast.makeText(this, "Number of days is required!", Toast.LENGTH_SHORT).show();
        } else if (!isValidNoDays(tvNoDays_str)) {
            Toast.makeText(this, "Number of days contains only numbers!", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "Retrieved result: ");
            Log.i(TAG, "Number of days: " + tvNoDays_str);
            Log.i(TAG, "Price: " + spPrice);
            Log.i(TAG, "Destination Type: " + category);
            Log.i(TAG, "Transportation Type: " + spTransportationType);

            // Prepare transaction materials
            int noDays = (int) Float.parseFloat(tvNoDays_str);
            JSONObject jsonFilterObject = getJsonFilterObject(noDays, spPrice, category, spTransportationType, preferences);

            // Return to MapFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.i(TAG, "Begin to Map");
            fragmentManager.beginTransaction().replace(R.id.clFilter, new MapFragment(INTENT, jsonFilterObject)).addToBackStack(TAG).commit();
        }
    }

    private boolean isValidNoDays(String tvNoDays_str) {
        Log.i(TAG, "isValidNoDays");

        String regexDoubleTypedNumber = "[0-9.]*";
        Pattern digitPattern = Pattern.compile(regexDoubleTypedNumber);

        Log.i(TAG, "isValidNoDays: " + digitPattern.matcher(tvNoDays_str).matches());
        return digitPattern.matcher(tvNoDays_str).matches();
    }

    @NonNull
    private List<Integer> getPreferences(@NonNull SeekBar[] _seekBars) {
        Log.i(TAG, "getPreferences");

        List<Integer> preferences = new ArrayList<>();
        HashSet<Integer> category_set = new HashSet<>();

        for (SeekBar currentSeekBar : _seekBars) {
            int currentProgress = currentSeekBar.getProgress();

            if (currentSeekBar.getProgress() > 0) {
                if (!category_set.contains(currentProgress)) {
                    category_set.add(currentProgress);
                    preferences.add(currentProgress);
                }
            }
        }

        Log.i(TAG, "getPreferences: " + preferences);
        return preferences;
    }

    @NonNull
    private String getCategory(@NonNull SeekBar[] _seekBars) {
        Log.i(TAG, "getCategory");

        List<String> categories = getCategoryList(new ArrayList<>(), _seekBars);

        if (categories.size() == 0) {
            return "";
        }
        return getCategoryString(new StringBuilder(), categories);
    }

    @NonNull
    private String getCategoryString(StringBuilder returnCategory, @NonNull List<String> categories) {
        Log.i(TAG, "getCategoryString");

        int lastCategoryIndex = categories.size() - 1;
        for (int i = 0; i < lastCategoryIndex; ++i) {
            returnCategory.append(categories.get(i));
            returnCategory.append(",");
        }
        returnCategory.append(categories.get(lastCategoryIndex));

        Log.i(TAG, "returnCategory: " + returnCategory);
        return returnCategory.toString();
    }

    private List<String> getCategoryList(List<String> categories, @NonNull SeekBar[] _seekBars) {
        Log.i(TAG, "getCategoryList");

        HashSet<String> category_set = new HashSet<>();

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
                }
            }
        }

        Log.i(TAG, "categories: " + categories);
        return categories;
    }

    @NonNull
    private JSONObject getJsonFilterObject(int _noDays, String _spPrice, String _category,
                                           String _spTransportationType, List<Integer> _preferences) throws JSONException {
        Log.i(TAG, "getJsonFilterObject");

        JSONObject jsonFilterObject = new JSONObject();

        jsonFilterObject.put("no_days", _noDays);
        jsonFilterObject.put("price", _spPrice);
        jsonFilterObject.put("destination_type", _category);
        jsonFilterObject.put("transportation_option", _spTransportationType);
        jsonFilterObject.put("preference_values", _preferences);

        Log.i(TAG, jsonFilterObject.toString());
        return jsonFilterObject;
    }

    @NonNull
    private Map<String, String> getBaseCategories() {
        Log.i(TAG, "getBaseCategories");

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