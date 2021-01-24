package com.mimdal.voicerecorder;

import android.app.Application;
import android.os.Environment;

import com.mimdal.voicerecorder.Helper.SingletonSharedPrefs;

public class MyAPP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SingletonSharedPrefs.init(getApplicationContext(), "VOICE_RECORDER_PREF");
    }

}
