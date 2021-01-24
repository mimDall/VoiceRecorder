package com.mimdal.voicerecorder.fragments;

import android.content.DialogInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mimdal.voicerecorder.Adapter.AudioListAdapter;
import com.mimdal.voicerecorder.Utils.DateConverter;
import com.mimdal.voicerecorder.Model.RecordingListItem;
import com.mimdal.voicerecorder.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class AudioListFragment extends Fragment implements AudioListAdapter.OnItemAudioListClickListener {

    private static final String TAG = "AudioListFragment";
    ConstraintLayout player_sheet;
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView audio_list;
    File[] recordingList;
    List<RecordingListItem> recordingListForRecycler;
    AudioListAdapter audioListAdapter;
    TextView player_sheet_title, player_sheet_fileName, player_sheet_start_time, player_sheet_end_time;
    ImageView player_sheet_play_btn, player_sheet_back_btn, player_sheet_forward_btn;
    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer = null;
    private File fileToPlay;


    private Handler playerHandler;
    private Runnable seekBarRunnable;
    private SeekBar player_sheet_seekBar;

    // for get duration of file
    private MediaMetadataRetriever mediaMetadataRetriever;


    private Runnable counterTimeRunnable;
    private long counterTime = -1;

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
        player_sheet_start_time = view.findViewById(R.id.player_sheet_start_time);
        player_sheet_end_time = view.findViewById(R.id.player_sheet_end_time);
        player_sheet_play_btn = view.findViewById(R.id.player_sheet_play_btn);
        player_sheet_back_btn = view.findViewById(R.id.player_sheet_back_btn);
        player_sheet_forward_btn = view.findViewById(R.id.player_sheet_forward_btn);
        player_sheet_seekBar = view.findViewById(R.id.player_sheet_seekBar);

        mediaPlayer = new MediaPlayer();

        bottomSheetBehavior = BottomSheetBehavior.from(player_sheet);
//        String path = requireActivity().getExternalFilesDir(null).getAbsolutePath(); // TODO:check permission
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/VoiceRecorder";
        recordingListForRecycler = new ArrayList();
        File directory = new File(path);
        recordingList = directory.listFiles();
        Arrays.sort(recordingList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {

                if (f1.lastModified() < f2.lastModified()) {

                    return 1;
                } else if (f1.lastModified() > f2.lastModified()) {

                    return -1;
                } else {
                    return 0;
                }

            }
        });


        for (File file : recordingList) {

            recordingListForRecycler.add(new RecordingListItem(file,
                    DateConverter.sizeStandardFormat(file.length()),
                    DateConverter.timeStandardFormat(Long.parseLong(getFileDuration(file)))
            ));

        }

        audioListAdapter = new AudioListAdapter(recordingListForRecycler, this);
        audio_list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        audio_list.setAdapter(audioListAdapter);


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

        player_sheet_play_btn.setOnClickListener(new View.OnClickListener() {
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


        player_sheet_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fileToPlay != null) {

                    playerSheetBackBtn();
                }

            }
        });
        player_sheet_forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fileToPlay != null) {

                    playerSheetForwardBtn();
                }

            }
        });


        player_sheet_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (fileToPlay != null) {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (fileToPlay != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });
    }

    private void playerSheetForwardBtn() {

        if((mediaPlayer.getCurrentPosition() + 3000) < mediaPlayer.getDuration()){

            new Thread(new Runnable() {
                @Override
                public void run() {

                    playerHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
                            player_sheet_seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            // start time forwards 3 secs
                            counterTime = counterTime + 3;
                            String counterTimeResult = DateConverter.counterTimeStandardFormat(Long.parseLong(getFileDuration(fileToPlay)), counterTime);
                            Log.d(TAG, "counterTimeResult im forward: " + counterTimeResult);
                            Log.d(TAG, "File duration in forward: " + getFileDuration(fileToPlay));
                            player_sheet_start_time.setText(counterTimeResult);
                        }
                    });

                }
            }).start();


        }else{

            mediaPlayer.seekTo(mediaPlayer.getDuration());
            player_sheet_seekBar.setProgress(mediaPlayer.getDuration());
            player_sheet_start_time.setText(player_sheet_end_time.getText().toString());

        }

    }

    private void playerSheetBackBtn() {

        if((mediaPlayer.getCurrentPosition() - 3000) > 0){

            new Thread(new Runnable() {
                @Override
                public void run() {

                    playerHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
                            player_sheet_seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            // start time forwards 3 secs
                            counterTime = counterTime - 3;
                            if(counterTime<0){
                                counterTime = 0;
                            }
                            String counterTimeResult = DateConverter.counterTimeStandardFormat(Long.parseLong(getFileDuration(fileToPlay)), counterTime);
                            Log.d(TAG, "counterTimeResult im forward: " + counterTimeResult);
                            Log.d(TAG, "File duration in forward: " + getFileDuration(fileToPlay));
                            player_sheet_start_time.setText(counterTimeResult);
                        }
                    });

                }
            }).start();


        }else{

            mediaPlayer.seekTo(0);
            player_sheet_seekBar.setProgress(0);
            counterTime = 0;
            String counterTimeResult = DateConverter.counterTimeStandardFormat(Long.parseLong(getFileDuration(fileToPlay)), counterTime);
            Log.d(TAG, "counterTimeResult im forward: " + counterTimeResult);
            Log.d(TAG, "File duration in forward: " + getFileDuration(fileToPlay));
            player_sheet_start_time.setText(counterTimeResult);
        }


    }


    private void playAudio(File fileToPlay) {
//      mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player_sheet_play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_pause_btn, null));
        player_sheet_title.setText("Playing");
        player_sheet_fileName.setText(fileToPlay.getName());
        isPlaying = true;

        player_sheet_seekBar.setMax(mediaPlayer.getDuration()); //  mSecond order
        playerHandler = new Handler();
        updateSeekBarPlayer();
        counterTime = -1;
        updateCounterTime();


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                player_sheet_title.setText("Finished");

            }
        });


    }

    private void stopAudio() {

        player_sheet_play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_play_btn, null));
        mediaPlayer.stop();
        player_sheet_title.setText("Stopped");
        isPlaying = false;
        playerHandler.removeCallbacks(seekBarRunnable);
        playerHandler.removeCallbacks(counterTimeRunnable);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        player_sheet_play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_play_btn, null));
        playerHandler.removeCallbacks(seekBarRunnable);
        playerHandler.removeCallbacks(counterTimeRunnable);

    }

    private void resumeAudio() {
        mediaPlayer.start();
        counterTime = player_sheet_seekBar.getProgress() / 1000;
        //Log.i(TAG, "counterTime: "+player_sheet_seekBar.getProgress()/1000);
        updateCounterTime();
        isPlaying = true;
        player_sheet_play_btn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.player_pause_btn, null));
        updateSeekBarPlayer();

    }

    private void updateSeekBarPlayer() {
        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                player_sheet_seekBar.setProgress(mediaPlayer.getCurrentPosition());
                playerHandler.postDelayed(this, 500);
            }
        };

        playerHandler.post(seekBarRunnable);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }

    @Override
    public void onItemAudioListClick(List<RecordingListItem> recordingListForRecycler, final int position, boolean longClick) {

        if (longClick) {
            onLongClickTask(recordingListForRecycler, position);
        } else {

            fileToPlay = recordingListForRecycler.get(position).getFile();

            if (isPlaying) {

                stopAudio();
                playAudio(fileToPlay);
            } else {
                playAudio(fileToPlay);
            }

            player_sheet_end_time.setText(DateConverter.timeStandardFormat(Long.parseLong(getFileDuration(fileToPlay))));

        }
    }


    private void onLongClickTask(final List<RecordingListItem> recordingListForRecycler, final int position) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Attention!")
                .setMessage("Delete this file?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                       /*
                            delete file from directory
                        */
                        File deletedFile = new File(recordingListForRecycler.get(position).getFile().getAbsolutePath());
                        boolean res = deletedFile.delete();
                        //delete file from files array and consequently from recycler view
                        if (res) {
                            recordingListForRecycler.remove(position);

                            /*
                                new data should pass to adapter
                             */
                            audioListAdapter.setData(recordingListForRecycler);
                            audioListAdapter.notifyDataSetChanged();
                            Toast.makeText(requireActivity(), "selected file delete.", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(requireActivity(), "selected file can Not delete. please try again.", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }


    private void updateCounterTime() {

        counterTimeRunnable = new Runnable() {
            @Override
            public void run() {
                counterTime++;
                String counterTimeResult = DateConverter.counterTimeStandardFormat(Long.parseLong(getFileDuration(fileToPlay)), counterTime);
                Log.d(TAG, "counterTimeResult: " + counterTimeResult);
                Log.d(TAG, "File duration: " + getFileDuration(fileToPlay));

                player_sheet_start_time.setText(counterTimeResult);
                playerHandler.postDelayed(this, 1000);
            }
        };

        playerHandler.post(counterTimeRunnable);


    }

    private String getFileDuration(File sourceFile) {

        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(sourceFile.getAbsolutePath());

        String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return duration;

    }
}













