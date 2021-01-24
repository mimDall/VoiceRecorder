package com.mimdal.voicerecorder.Utils;


import android.util.Log;

public class DateConverter {

    private static final String TAG = "Converter";

    public static String timeStandardFormat(long duration) {

        Log.d(TAG, "duration: " + duration);

        long durationInSeconds = duration / 1000;
        long seconds;
        long minutes;
        long hours;

        seconds = duration;

        if (seconds < 60) {

            return "00:" + ((seconds < 10) ? "0" + seconds : seconds);
        }


        seconds = durationInSeconds % 60;
        minutes = durationInSeconds / 60;

        if (minutes < 60) {
            return ((minutes < 10) ? "0" + minutes : "" + minutes) + ":" + ((seconds < 10) ? "0" + seconds : "" + seconds);
        }

        hours = minutes / 60;
        minutes = minutes % 60;

        return ((hours < 10) ? "0" + hours : "" + hours) + ":" + ((minutes < 10) ? "0" + minutes : "" + minutes) + ":" + ((seconds < 10) ? "0" + seconds : "" + seconds);

    }

    public static String sizeStandardFormat(long size) {

        long sizeInKb = size / 1024;

        if (sizeInKb == 0) {
            return size + "Bytes";
        }

        if (sizeInKb > 1024) {
            return (sizeInKb / 1024) + "Mb";
        } else {
            return sizeInKb + "Kb";
        }

    }

    public static String counterTimeStandardFormat(long duration, long counterTime) {

        long seconds;
        long minutes;
        long hours;

        String result="";

        if (counterTime < duration) {

            if (counterTime < 3600) {

                seconds = counterTime % 60;
                minutes = counterTime / 60;

                result = ((minutes < 10 ? "0" + minutes : "" + minutes) + ":" +
                        (seconds < 10 ? "0" + seconds : "" + seconds));

            } else {

                seconds = counterTime % 60;
                minutes = counterTime / 60;
                hours = minutes / 60;
                minutes = minutes % 60;

                result =  ((hours < 10 ? "0" + hours : "" + hours) + ":" +
                        (minutes < 10 ? "0" + minutes : "" + minutes) + ":" +
                        (seconds < 10 ? "0" + seconds : "" + seconds));

            }

        }


        return result;

    }
}
