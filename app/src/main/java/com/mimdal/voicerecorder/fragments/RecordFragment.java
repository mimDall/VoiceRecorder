package com.mimdal.voicerecorder.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.mimdal.voicerecorder.Helper.PermissionUtils;
import com.mimdal.voicerecorder.R;
import com.visualizer.amplitude.AudioRecordView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {

    private ImageView record_list_btn;
    private ImageView record_btn;
    private NavController navController;
    private boolean recording = false;
    private static final String TAG = "RecordFragment";
    private Chronometer record_chronometer;
    private TextView record_fileName;

    private PermissionUtils permissionsUtils;

    private final String[] PERMISSIONS = {"android.permission.RECORD_AUDIO",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    private final int REQUEST_PERMISSIONS_CODE = 100;


    private MediaRecorder mediaRecorder;

    private AudioRecordView audioRecordView;
    private Handler handlerWave;
    private Runnable runnableWave;

    private long startTime;
    private long endTime;
    private long recordingDuration;


    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        record_list_btn = view.findViewById(R.id.record_list_btn);
        record_btn = view.findViewById(R.id.record_btn);
        record_chronometer = view.findViewById(R.id.record_chronometer);
        record_fileName = view.findViewById(R.id.record_fileName);
        navController = Navigation.findNavController(view);


        record_list_btn.setOnClickListener(this);
        record_btn.setOnClickListener(this);


        audioRecordView = view.findViewById(R.id.audioRecordView);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.record_list_btn:

                if (recording) {

                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Warning!")
                            .setMessage("Audio still recording.\nAre you sure to stop recording?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    stopRecording();
                                    recording = false;
                                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);

                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();

                } else {

                    RecordFragmentDirections.ActionRecordFragmentToAudioListFragment action = RecordFragmentDirections.actionRecordFragmentToAudioListFragment();
                    action.setDuration(28);

                    navController.navigate(action);

//                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }
                break;

            case R.id.record_btn:
                if (recording) {

                    record_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.record_btn_stopped, null));
                    recording = false;
                    stopRecording();

                } else {

                    new PermissionUtils(
                            requireActivity(),
                            new PermissionUtils.PermissionAskListener() {
                                @Override
                                public void onPermissionGranted() {
                                    record_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.record_btn_recording, null));
                                    recording = true;
                                    startRecording();
                                }

                                @Override
                                public void onPermissionRequest() {

                                    ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST_PERMISSIONS_CODE);

                                }

                                @Override
                                public void onPermissionPreviouslyDenied() {
                                    new AlertDialog.Builder(requireActivity())
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

                                    new AlertDialog.Builder(requireActivity())
                                            .setTitle("permission disabled")
                                            .setMessage("enable permission in following path. setting>user>permission")
                                            .setPositiveButton("go to setting", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    requireActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
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
                            }, PERMISSIONS)
                            .checkPermissions();

                }

                break;
        }
    }

    private void stopRecording() {

        record_chronometer.stop();
        mediaRecorder.stop();
        endTime = SystemClock.elapsedRealtime();
        recordingDuration = startTime - endTime;
        mediaRecorder.release();
        mediaRecorder = null;
        record_fileName.setText("press the mic button\nto start recording");
        handlerWave.removeCallbacks(runnableWave);

    }

    private void startRecording() {

        record_chronometer.setBase(SystemClock.elapsedRealtime());
        startTime = SystemClock.elapsedRealtime();
        record_chronometer.start();


//        String recordPath = requireActivity().getExternalFilesDir(null).getAbsolutePath();

        File file = new File(Environment.getExternalStorageDirectory(), "VoiceRecorder");

        if (!file.exists()) {
            file.mkdirs();
        }
        /*
            use this pattern in order to make a unique name
         */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss", Locale.CANADA);
        Date currentDate = new Date();

        String recordFile = "Recording_" + simpleDateFormat.format(currentDate) + ".MPEG4";
        record_fileName.setText("Recording, File Name: " + recordFile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncodingBitRate(16 * 44100);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(file.getPath() + "/" + recordFile);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
//            e.printStackTrace();

            Log.d(TAG, "startRecording: " + e.getStackTrace());
            Log.d(TAG, "startRecording: " + e.getMessage());
        }
        handlerWave = new Handler();
        updateWave();

    }

    private void updateWave() {

        runnableWave = new Runnable() {
            @Override
            public void run() {
                int currentMaxAmplitude = mediaRecorder.getMaxAmplitude();

                Log.d(TAG, "currentMaxAmplitude: " + currentMaxAmplitude);
                audioRecordView.update(currentMaxAmplitude);

                //audioRecordView.recreate();

                handlerWave.postDelayed(this, 100);
            }
        };

        handlerWave.post(runnableWave);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (recording) {

                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Warning!")
                            .setMessage("Audio still recording.\nAre you sure to stop recording and exit application?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    stopRecording();
                                    Log.d(TAG, "exit app");
                                    requireActivity().finish();

                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();


                } else {
                    requireActivity().finish();

                }


            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}