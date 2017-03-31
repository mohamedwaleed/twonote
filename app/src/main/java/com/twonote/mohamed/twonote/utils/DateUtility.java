package com.twonote.mohamed.twonote.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public static Date getDataObject(String dateString, String formatString) throws ParseException {
        DateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
        Date date = format.parse(dateString);
        return date;
    }
}
