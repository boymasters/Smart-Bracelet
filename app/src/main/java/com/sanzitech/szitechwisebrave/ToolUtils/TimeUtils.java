package com.sanzitech.szitechwisebrave.ToolUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by android on 2018/01/11.
 */

public class TimeUtils {

    public static final String FORMAT_HOUR_MINITE_1 = "HH:mm";
    public static final String FORMAT_HOUR_MINITE_2 = "H时mm分";
    public static final String FORMAT_YEAR_MONTH_DAY_1 = "yyyy-MM-dd";
    public static final String FORMAT_YEAR_MONTH_DAY_2 = "yyyy/MM/dd";

    public static String formatDate(long timeInMillis, String pattern) {
        return formatDate(new Date(timeInMillis), pattern);
    }

    public static String formatDate(Calendar calendar, String pattern) {
        return formatDate(calendar.getTime(), pattern);
    }

    public static String formatDate(Date date, String pattern) {
        return formatDate(date, pattern, null);
    }

    public static String formatDate(Date date, String pattern, Locale locale) {
        SimpleDateFormat simpleDateFormat;
        if (locale == null) {
            simpleDateFormat = new SimpleDateFormat(pattern);
        } else {
            simpleDateFormat = new SimpleDateFormat(pattern, locale);
        }
        return simpleDateFormat.format(date);
    }
}
