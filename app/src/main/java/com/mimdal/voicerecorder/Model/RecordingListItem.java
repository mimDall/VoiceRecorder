package com.mimdal.voicerecorder.Model;

import java.io.File;

public class RecordingListItem{

    private File file;
    private String duration;
    private String size;

    public RecordingListItem(File file, String duration, String size) {
        this.file = file;
        this.duration = duration;
        this.size = size;
    }

    public RecordingListItem() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

}
