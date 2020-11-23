package com.mimdal.voicerecorder.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mimdal.voicerecorder.Adapter.AudioListAdapter;
import com.mimdal.voicerecorder.R;

import java.io.File;


public class AudioListFragment extends Fragment {

    ConstraintLayout player_sheet;
    BottomSheetBehavior bottomSheetBehavior;
    RecyclerView audio_list;
    File[] recordingList;
    AudioListAdapter audioListAdapter;

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
        bottomSheetBehavior = BottomSheetBehavior.from(player_sheet);
        String path = requireActivity().getExternalFilesDir(null).getAbsolutePath();
        File directory = new File(path);
        recordingList = directory.listFiles();

        audioListAdapter = new AudioListAdapter(recordingList);
        audio_list.setLayoutManager(new LinearLayoutManager(requireActivity()));
        audio_list.setAdapter(audioListAdapter);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }
}