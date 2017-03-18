package com.twonote.mohamed.twonote.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mohamed on 17/03/17.
 */

public class DateUtility {

    public static String getCurrentDate(String dataFormat) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(dataFormat);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
