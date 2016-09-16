package com.example.nasko.whisper.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageSeparatorDateFormatter implements DateFormatter {

    private static final String YESTERDAY_FORMAT = "Yesterday";
    private static final DateFormat WEEKDAY_FORMAT = new SimpleDateFormat("E d-M", Locale.US);
    private static final DateFormat MONTHLY_FORMAT = new SimpleDateFormat("d MMMM", Locale.US);
    private static final int TO_DAYS_DENOMINATOR = 24 * 60 * 60 * 1000;

    public String getStringFormat(Date now, Date other) {
        long daysDiff = (now.getTime() - other.getTime()) / TO_DAYS_DENOMINATOR;
        if (daysDiff == 1) {
            return YESTERDAY_FORMAT;
        } else if (daysDiff <= 6) {
            return WEEKDAY_FORMAT.format(other);
        } else {
            return MONTHLY_FORMAT.format(other);
        }
    }
}
