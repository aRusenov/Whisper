package com.example.nasko.whisper.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    private static final String YESTERDAY_FORMAT = "Yesterday";
    private static final DateFormat WEEKDAY_FORMAT = new SimpleDateFormat("E");
    private static final DateFormat MONTHLY_FORMAT = new SimpleDateFormat("W d");
    private static final int TO_DAYS_DENOMINATOR = 24 * 60 * 60 * 1000;

    public static String getStringFormat(Date now, Date other) {
        long daysDiff = (now.getTime() - now.getTime()) / TO_DAYS_DENOMINATOR;
        if (daysDiff == 1) {
            return YESTERDAY_FORMAT;
        } else if (daysDiff <= 6) {
            return WEEKDAY_FORMAT.format(other);
        } else {
            return MONTHLY_FORMAT.format(other);
        }
    }
}
