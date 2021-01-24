package com.mimdal.voicerecorder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mimdal.voicerecorder.Utils.FormatTime;
import com.mimdal.voicerecorder.Model.RecordingListItem;
import com.mimdal.voicerecorder.R;

import java.util.List;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioListViewHolder> {

    private List<RecordingListItem> recordingListForRecycler;
    private FormatTime formatTime;
    private OnItemAudioListClickListener onItemAudioListClickListener;

    public AudioListAdapter(List<RecordingListItem> recordingListForRecycler, OnItemAudioListClickListener onItemAudioListClickListener) {
        this.recordingListForRecycler = recordingListForRecycler;
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

        holder.item_title.setText(recordingListForRecycler.get(position).getFile().getName());
        holder.itemDate.setText(formatTime.getFormatTime(recordingListForRecycler.get(position).getFile().lastModified()));
        holder.itemDuration.setText(recordingListForRecycler.get(position).getDuration());
        holder.itemSize.setText(recordingListForRecycler.get(position).getSize());
    }

    @Override
    public int getItemCount() {
        return recordingListForRecycler.size();
    }

    public class AudioListViewHolder extends RecyclerView.ViewHolder {

        TextView item_title;
        TextView itemDate;
        TextView itemSize;
        TextView itemDuration;

        public AudioListViewHolder(@NonNull View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
            itemDate = itemView.findViewById(R.id.itemDate);
            itemSize = itemView.findViewById(R.id.itemSize);
            itemDuration = itemView.findViewById(R.id.itemDuration);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemAudioListClickListener.onItemAudioListClick(recordingListForRecycler, getAdapterPosition(), false);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    onItemAudioListClickListener.onItemAudioListClick(recordingListForRecycler, getAdapterPosition(), true);

//
//                    File myFile = new File(allFiles[getAdapterPosition()].getAbsolutePath());
//                    boolean res = myFile.delete();
//
//                    if (res) {
//                        //convert files array to arrayList
//                        //this conversion is done due to removal element easily.
//                        List<File> filesList = new ArrayList<>(Arrays.asList(allFiles));
//                        //remove certain file from list
//                        filesList.remove(allFiles[getAdapterPosition()]);
//                        //convert list to array
//                        allFiles = new File[filesList.size()];
//                        filesList.toArray(allFiles);
//                        // notifyItemRemoved(getAdapterPosition());
//                        notifyDataSetChanged();
//                        Toast.makeText(context, "selected file delete.", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        Toast.makeText(context, "selected file can Not delete. please try agein.", Toast.LENGTH_SHORT).show();
//                    }
                    return true;
                }
            });
        }
    }

    public void setData(List<RecordingListItem> recordingListForRecycler){
        this.recordingListForRecycler = recordingListForRecycler;
    }

    public interface OnItemAudioListClickListener {

        void onItemAudioListClick(List<RecordingListItem> recordingListForRecycler, int position, boolean longClick);

    }
}

