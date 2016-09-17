package com.example.nasko.whisper.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Date;

public class LastMessageDateFormatter implements DateFormatter {

    private long nowMillis = new Date().getTime();

    @Override
    public String getStringFormat(Context context, Date date) {
        return (String) DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                new Date().getTime(),
                0);
    }
}
