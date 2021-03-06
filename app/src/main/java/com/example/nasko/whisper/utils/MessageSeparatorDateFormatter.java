package com.example.nasko.whisper.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Date;

public class MessageSeparatorDateFormatter implements DateFormatter {

    public String getStringFormat(Context context, Date date) {
        return (String) DateUtils.getRelativeDateTimeString(
                context,
                date.getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                0);
    }
}
