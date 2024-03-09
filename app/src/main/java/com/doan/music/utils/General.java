package com.doan.music.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class General {
    public static String convertTimeToString(int intTime) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(intTime),
                TimeUnit.MILLISECONDS.toSeconds(intTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(intTime)));
    }

    public static String convertTimeToString(String longStringTime) {
        return convertTimeToString(Integer.parseInt(longStringTime));
    }
}
