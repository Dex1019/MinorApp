

package com.orpheusdroid.screenrecorder.adapter;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;


public class Video implements Comparable<Video> {
    private String FileName;
    private Uri file;
    private Bitmap thumbnail;
    private Date lastModified;
    private boolean isSection = false;

    public Video(boolean isSection, Date lastModified) {
        this.isSection = isSection;
        this.lastModified = lastModified;
    }

    public Video(String fileName, Uri file, Bitmap thumbnail, Date lastModified) {
        FileName = fileName;
        this.file = file;
        this.thumbnail = thumbnail;
        this.lastModified = lastModified;
    }

    public String getFileName() {
        return FileName;
    }

    public Uri getFile() {
        return file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public boolean isSection() {
        return isSection;
    }

    @Override
    public int compareTo(Video video) {
        return getLastModified().compareTo(video.getLastModified());
    }
}
