package com.mimdal.voicerecorder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mimdal.voicerecorder.FormatTime;
import com.mimdal.voicerecorder.R;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioListViewHolder> {

    private File[] allFiles;
    private FormatTime formatTime;
    private OnItemAudioListClickListener onItemAudioListClickListener;

    public AudioListAdapter(File[] allFiles, OnItemAudioListClickListener onItemAudioListClickListener) {
        this.allFiles = allFiles;
        this.onItemAudioListClickListener = onItemAudioListClickListener;
    }

    @NonNull
    @Override
    public AudioListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_list_item, parent, false);
        formatTime = new FormatTime();
        return new AudioListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioListViewHolder holder, int position) {

        holder.item_title.setText(allFiles[position].getName());
        holder.itemDate.setText(formatTime.getFormatTime(allFiles[position].lastModified()));

    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class AudioListViewHolder extends RecyclerView.ViewHolder{

        TextView item_title;
        TextView itemDate;

        public AudioListViewHolder(@NonNull View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
            itemDate = itemView.findViewById(R.id.itemDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemAudioListClickListener.onItemAudioListClick(allFiles[getAdapterPosition()], getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemAudioListClickListener {

        public void onItemAudioListClick (File file, int position);

    }
}

