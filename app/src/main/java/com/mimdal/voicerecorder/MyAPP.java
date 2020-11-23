package com.mimdal.voicerecorder;

import android.app.Application;
import android.os.Environment;

import com.mimdal.voicerecorder.Helper.SingletonSharedPrefs;

class MyAPP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SingletonSharedPrefs.init(this, "VOICE_RECORDER_PREF");
    }

}
