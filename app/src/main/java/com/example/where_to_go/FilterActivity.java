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
        Spinner spDestinationType = (Spinner) findViewById(R.id.spDestinationType);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            receiveFilterResult(v, tvNoDays, tvPriceUnder, spDestinationType);
        });
    }

    // HELPER METHODS

    private void receiveFilterResult(View v, EditText _tvNoDays, EditText _tvPriceUnder, Spinner _spDestinationType) {
        String tvNoDays_str = _tvNoDays.getText().toString();
        String tvPriceUnder_str = _tvPriceUnder.getText().toString();
        String spDestinationType = _spDestinationType.getSelectedItem().toString();


        if (!tvNoDays_str.isEmpty() && !tvPriceUnder_str.isEmpty() && !spDestinationType.isEmpty()) {
            Log.i(TAG, "Retrieved result: " + _tvNoDays + " " + _tvPriceUnder);
            int noHours = (int) (Float.parseFloat(tvNoDays_str) * HOURS_PER_DAY);
            int priceUnder = Integer.parseInt(tvPriceUnder_str);

            JSONObject jsonResult = new JSONObject();
            try {
                jsonResult.put("no_hours", noHours);
                jsonResult.put("price_under", priceUnder);
                jsonResult.put("destination_type", spDestinationType);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            Log.i(TAG, "Begin to Map");
            fragmentManager.beginTransaction().replace(R.id.llFilter, new MapFragment(INTENT, jsonResult)).commit();
        }
    }
}