package com.example.where_to_go.utilities;

import android.util.Log;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import java.util.Comparator;

public class SeekBarComparator implements Comparator<SeekBar> {
    private static final String TAG = "SeekBarComparator";

    @Override
    public int compare(@NonNull SeekBar seekBar1, @NonNull SeekBar seekBar2) {
        Log.i(TAG, "compare 2 seekbars in descending order");

        int progress1 = seekBar1.getProgress();
        int progress2 = seekBar2.getProgress();

        if (progress1 < progress2) {
            return 1;
        } else if (progress1 > progress2) {
            return -1;
        }
        return 0;
    }
}
