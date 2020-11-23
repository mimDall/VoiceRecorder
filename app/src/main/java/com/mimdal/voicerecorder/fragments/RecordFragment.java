package com.mimdal.voicerecorder.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.mimdal.voicerecorder.Helper.PermissionUtils;
import com.mimdal.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment implements View.OnClickListener {

    ImageView record_list_btn;
    ImageView record_btn;
    NavController navController;
    private boolean recording = false;
    private static final String TAG = "RecordFragment";
    Chronometer record_chronometer;
    TextView record_fileName;

    String[] permission = {"android.permission.RECORD_AUDIO"};
    private int REQUEST_PERMISSION_CODE = 100;

    MediaRecorder mediaRecorder;

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


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.record_list_btn:
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                break;

            case R.id.record_btn:
                if (recording) {

                    record_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.record_btn_stopped, null));
                    recording = false;
                    stopRecording();

                } else {

                    record_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.record_btn_recording, null));
                    recording = true;
                    startRecording();

                    new PermissionUtils(getActivity(), new PermissionUtils.PermissionAskListener() {
                        @Override
                        public void onPermissionGranted() {

                        }

                        @Override
                        public void onPermissionRequest() {

                            ActivityCompat.requestPermissions(requireActivity(), permission, REQUEST_PERMISSION_CODE);
                        }

                        @Override
                        public void onPermissionPreviouslyDenied() {

                        }

                        @Override
                        public void onPermissionDisabled() {

                        }
                    }, permission).checkPermissions();


                }

                break;
        }
    }

    private void stopRecording() {

        record_chronometer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        record_fileName.setText("press the mic button\nto start recording");
    }

    private void startRecording() {

        record_chronometer.setBase(SystemClock.elapsedRealtime());
        record_chronometer.start();
        String recordPath = requireActivity().getExternalFilesDir(null).getAbsolutePath();

        /*
            use this pattern in order to make a unique name
         */
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss", Locale.CANADA);
        Date currentDate = new Date();

        String recordFile = "Recording_" + simpleDateFormat.format(currentDate) + ".3gp";
        record_fileName.setText("Recording, File Name: "+recordFile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(recordPath+"/"+recordFile);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();

    }


}