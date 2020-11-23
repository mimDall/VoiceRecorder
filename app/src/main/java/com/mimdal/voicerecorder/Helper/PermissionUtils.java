package com.mimdal.voicerecorder.Helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    /*
        inside the shared preference file, store information about weather the application
        launches for first time or not.
     */
    private Activity activity;
    private @NonNull
    PermissionAskListener permissionAskListener;
    private String[] permissions;
    private static final String PREFS_FIRST_TIME_KEY = "is_app_launched_first_time";

    public PermissionUtils(Activity activity, @NonNull PermissionAskListener permissionAskListener, String[] permissions) {
        this.activity = activity;
        this.permissionAskListener = permissionAskListener;
        this.permissions = permissions;
    }

    public interface PermissionAskListener {

        void onPermissionGranted();

        /*
            user has already grant the permission
            the app must had been launched earlier and user must "allowed" the permission
         */

        void onPermissionRequest();
        /*
            the app runs for first time, no additional dialog is needed
            just request permission
         */

        void onPermissionPreviouslyDenied();
        /*
            the app was launched earlier and user denied the permission BUT
            user had not clicked "DO NOT SHOW AGAIN"
            so, an additional dialog is needed in order to explain why this permission would be critical
         */


        void onPermissionDisabled();
        /*
            the app was launched earlier and user denied the permission AND
            user had clicked "DO NOT SHOW AGAIN"
            it is good to aware user by toast/dialog/... that denied permission along with clicked "DO NOT SHOW AGAIN"
            you can direct user to setting>app>permission by intent to allow permission
         */


    }


    private boolean isRunTimePermissionRequired() {

        return (Build.VERSION.SDK_INT >= 23);
    }

    private boolean getApplicationRunFirstTime() {

        return SingletonSharedPrefs.getInstance().readBoolean(PREFS_FIRST_TIME_KEY, true);
    }

    private void setApplicationRunFirstTime() {

        SingletonSharedPrefs.getInstance().writeBoolean(PREFS_FIRST_TIME_KEY, false);
    }

    public void checkPermissions() {

        if (permissionAskListener == null) {
            throw new IllegalArgumentException();
        }

        if (isRunTimePermissionRequired()) {


            if (checkAllPermissions()) {


                if (checkForRequestPermissionRationale()) {

                        /*
                            user denied permission earlier WITHOUT checking "never ask again"
                         */
                    permissionAskListener.onPermissionPreviouslyDenied();

                } else {

                    if (getApplicationRunFirstTime()) {
                            /*
                                application launches first time
                             */
                        setApplicationRunFirstTime();
                        permissionAskListener.onPermissionRequest();

                    } else {
                            /*
                                user denied permisson WITH check "never ask again"
                             */

                        permissionAskListener.onPermissionDisabled();

                    }

                }
            } else {
                permissionAskListener.onPermissionGranted();

            }

        }

    }

    private boolean checkAllPermissions() {

        boolean result = false;

        for (String permission : permissions) {

            result = (result || (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED));
        }

        return result;
    }


    private boolean checkForRequestPermissionRationale() {
        boolean result = false;

        for (String permission : permissions) {

            result = (result || ActivityCompat.shouldShowRequestPermissionRationale(activity, permission));
        }

        return result;
    }


}