package com.mimdal.voicerecorder.Helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class PermissionsAskListener implements PermissionUtils.PermissionAskListener{

    private Activity activity;
    private String[] permissions;
    private int permissionCode;


    public PermissionsAskListener(Activity activity, String[] permissions, int permissionCode) {
        this.activity = activity;
        this.permissions = permissions;
        this.permissionCode = permissionCode;
    }

    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionRequest() {

        ActivityCompat.requestPermissions(activity, permissions, permissionCode);
    }

    @Override
    public void onPermissionPreviouslyDenied() {

        new AlertDialog.Builder(activity)
                .setTitle("permission required")
                .setMessage("permission(s) needed for app to work well.")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        onPermissionRequest();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    @Override
    public void onPermissionDisabled() {

        new AlertDialog.Builder(activity)
                .setTitle("permission disabled")
                .setMessage("enable permission in following path. setting>user>permission")
                .setPositiveButton("go to setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create()
                .show();


    }
}
