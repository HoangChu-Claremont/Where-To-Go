package com.example.where_to_go.utilities;
import android.widget.SeekBar;
import java.util.Comparator;

public class SeekBarComparator implements Comparator<SeekBar> {
    @Override
    public int compare(SeekBar seekBar1, SeekBar seekBar2) {
        int progress1 = seekBar1.getProgress();
        int progress2 = seekBar2.getProgress();

        if (progress1 < progress2) { // Descending Order
            return 1;
        } else if (progress1 > progress2) {
            return -1;
        }
        return 0;
    }
}
