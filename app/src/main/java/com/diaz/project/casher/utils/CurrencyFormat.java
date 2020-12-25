package com.diaz.project.casher.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyFormat {

    @SuppressWarnings("UnusedReturnValue")
    public static String convertAndFormat(int s) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp.");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        format.setDecimalFormatSymbols(formatRp);
        return format.format(s);
    }
}
