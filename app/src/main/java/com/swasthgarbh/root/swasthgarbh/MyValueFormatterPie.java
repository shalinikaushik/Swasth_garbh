package com.swasthgarbh.root.swasthgarbh;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class MyValueFormatterPie implements IValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatterPie() {
        mFormat = new DecimalFormat("###,###,###"); // use no decimals
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        // write your logic here
//        if(value < 10) return value;

//        return mFormat.format(value) + "%"; // in case you want to add percent
        return mFormat.format((int)value); // in case you want to add percent
    }
}
