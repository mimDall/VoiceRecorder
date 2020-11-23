package com.mimdal.voicerecorder.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mimdal.voicerecorder.Adapter.AudioListAdapter;
import com.mimdal.voicerecorder.R;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements AudioListAdapter.OnItemAudioListClickListener {

    private static final String TAG = "AudioListFragment";
    ConstraintLayout player_sheet;
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView audio_list;
    File[] recordingList;
    AudioListAdapter audioListAdapter;
    TextView player_sheet_title, player_sheet_fileName;
    ImageView play_btn;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer = null;
    private File fileToPlay;


    private Handler playerHandler;
    private Runnable playerRunnable;
    private SeekBar player_sheet_seekBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        player_sheet = view.findViewById(R.id.player_sheet);
        audio_list = view.findViewById(R.id.audio_list);
        player_sheet_title = view.findViewById(R.id.player_sheet_title);
        player_sheet_fileName = view.findViewById(R.id.player_sheet_fileName);
        play_btn = view.findViewById(R.id.play_btn);


        bottomSheetBehavior = BottomSheetBehavior.from(player_sheet);
        String path = requireActivity().getExternalFilesDir(null).getAbsolutePath();
        File directory = new File(path);
        recordingList = directory.listFiles();

        audioListAdapter = new AudioListAdapter(recordingList, this);
        audio_list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        audio_list.setAdapter(audioListAdapter);

        player_sheet_seekBar = view.findViewById(R.id.player_sheet_seekBar);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPlaying) {
                    pauseAudio();
                } else {

                    if (fileToPlay != null) {

                        resumeAudio();
                    }
                }
            }
        });

        player_sheet_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if(fileToPlay!=null){
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(fileToPlay!=null){
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });
    }

    @Override
    public void onItemAudioListClick(File file, int position) {
        fileToPlay = file;
        Log.d(TAG, file.getName() + "");

        if (isPlaying) {

            stopAudio();
            playAudio(fileToPlay);
        } else {
            playAudio(fileToPlay);
        }
    }

    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_pause_btn, null));
        player_sheet_title.setText("Playing");
        player_sheet_fileName.setText(fileToPlay.getName());
        isPlaying = true;

        player_sheet_seekBar.setMax(mediaPlayer.getDuration());
        playerHandler = new Handler();
        updateSeekBarPlayer();


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                player_sheet_title.setText("Finished");
                stopAudio();

            }
        });


    }

    private void stopAudio() {

        play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_play_btn, null));
        mediaPlayer.stop();
        player_sheet_title.setText("Stopped");
        isPlaying = false;
        playerHandler.removeCallbacks(playerRunnable);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_play_btn, null));
        playerHandler.removeCallbacks(playerRunnable);

    }

    private void resumeAudio() {
        mediaPlayer.start();
        isPlaying = true;
        play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_pause_btn, null));
        updateSeekBarPlayer();

    }

    private void updateSeekBarPlayer() {
        playerRunnable = new Runnable() {
            @Override
            public void run() {
                player_sheet_seekBar.setProgress(mediaPlayer.getCurrentPosition());
                playerHandler.postDelayed(this, 500);
            }
        };

        playerHandler.post(playerRunnable);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }
}