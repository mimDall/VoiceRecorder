package com.mimdal.voicerecorder.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FormatTime {

    private Date now;

    public String getFormatTime(long time) {

       now = new Date();
        String res = "";

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() -time);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() -time);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() -time);

        if (seconds < 60) {
            res = "just now";
        } else if (minutes == 1) {
            res = "a minute ago";
        } else if (minutes > 1 && minutes < 60) {
            res = minutes + " minutes ago";
        } else if (hours == 1) {
            res = "an hour ago";
        } else if (hours > 1 && hours < 24) {
            res = hours + " hours ago";
        } else if (days == 1) {
            res = "a day ago";
        } else {
            res =  formatDate();
        }
        return res;
    }

    private String formatDate() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(now);
    }
}
