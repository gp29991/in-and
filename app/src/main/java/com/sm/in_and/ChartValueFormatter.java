package com.sm.in_and;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

public class ChartValueFormatter extends ValueFormatter {
    @Override
    public String getBarLabel(BarEntry barEntry) {
        return String.format(new Locale("pl","PL"), "%.2f", barEntry.getY());
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        return String.format(new Locale("pl","PL"), "%.2f", pieEntry.getY());
    }
}
