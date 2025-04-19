package com.example.custommediaplayer.models;

import java.util.Collections;
import java.util.List;

public class Playlist {
    private String name;
    private String author;
    private List<Song> songs;

    public Playlist(String name, String author, List<Song> songs)
    {
        this.name = name;
        this.author = author;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }
    public List<Song> getSongs() {
        return songs;
    }
    public void addSong(Song s)
    {
        songs.add(s);
    }
    public void removeSong(int id)
    {
        songs.remove(id);
    }
    public void swapSongs(int id1, int id2)
    {
        Collections.swap(songs, id1, id2);
    }
    public Song getSong(int id)
    {
        return songs.get(id);
    }
    public int getSize()
    {
        return songs.size();
    }

}
