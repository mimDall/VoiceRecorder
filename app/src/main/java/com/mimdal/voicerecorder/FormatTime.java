package com.mimdal.voicerecorder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FormatTime {

    public String getFormatTime(long time) {

        Date now = new Date();
        String res = "";

        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long days = TimeUnit.MILLISECONDS.toDays(time);

        if (seconds < 60) {
            res = "just now";
        } else if (minutes == 1) {
            res = "a minute ago";
        } else if (minutes > 1 && minutes < 60) {
            res = minutes + "minutes ago";
        } else if (hours == 1) {
            res = "an hour ago";
        } else if (hours > 1 && hours < 24) {
            res = hours + "hours ago";
        } else if (days == 1) {
            res = "a day ago";
        } else {
            res = days + "days ago";
        }

        return res;

    }
}
