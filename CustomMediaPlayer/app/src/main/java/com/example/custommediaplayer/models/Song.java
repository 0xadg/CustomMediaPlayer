package com.example.custommediaplayer.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class Song {
    private String title;
    private String artist;
    private int duration;
    private int year;
    private String path;
    private Bitmap cover;
    public Song(String title, String artist, int duration, int year,String path, Bitmap cover)
    {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.year = year;
        this.path = path;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public int getYear() {
        return year;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getCover() {
        return cover;
    }
}
