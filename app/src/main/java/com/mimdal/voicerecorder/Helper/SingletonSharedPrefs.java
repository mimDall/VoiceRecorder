package com.mimdal.voicerecorder.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 * This is a singleTone class for sharedPreference, with all read and write methods.
 *
 *
 *
 */

public class SingletonSharedPrefs {

    private static SingletonSharedPrefs instance;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * This method should be called in onCreate method at custom Application class
     * @param context
     * @param name
     */



    public synchronized static void init(Context context, String name) {

        instance = new SingletonSharedPrefs(context, name);

    }

    public static SingletonSharedPrefs getInstance() {

        if (instance == null) {

        }

        return instance;
    }

    private SingletonSharedPrefs(Context context, String name) {

        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }


    public void writeString(String key, String value) {

        editor.putString(key, value)
                .commit();
    }

    public void writeInt(String key, int value) {


        editor.putInt(key, value)
                .commit();

    }

    public void writeBoolean(String key, boolean value) {

        editor.putBoolean(key, value)
                .commit();

    }

    public void writeFloat(String key, float value) {


        editor.putFloat(key, value)
                .commit();
    }

    public void writeLong(String key, long value) {


        editor.putLong(key, value)
                .commit();

    }


    public String readString(String key, String defValue) {

        return sharedPreferences.getString(key, defValue);

    }

    public int readInt(String key, int defValue) {

        return sharedPreferences.getInt(key, defValue);

    }

    public boolean readBoolean(String key, boolean defValue) {

        return sharedPreferences.getBoolean(key, defValue);

    }

    public float readFloat(String key, float defValue) {

        return sharedPreferences.getFloat(key, defValue);

    }

    public long readLong(String key, long defValue) {

        return sharedPreferences.getLong(key, defValue);

    }

    public void remove(String key) {

        editor.remove(key)
                .commit();
    }

    public void clearAll() {
        editor.clear()
                .commit();
    }
}