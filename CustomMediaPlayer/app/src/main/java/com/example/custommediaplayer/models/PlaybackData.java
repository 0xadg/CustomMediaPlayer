package com.example.custommediaplayer.models;

import androidx.media3.common.MediaItem;

import java.util.List;

public class PlaybackData {
    private String activePlaylistName;
    private Playlist activePlaylist;
    private int activeSong;
    private List<MediaItem> activePlaylistSongs;

    public PlaybackData(String activePlaylistName, Playlist activePlaylist, int activeSong)
    {
        this.activePlaylistName = activePlaylistName;
        this.activePlaylist = activePlaylist;
        this.activeSong = activeSong;
    }


    public void setActivePlaylistSongs(List<MediaItem> activePlaylistSongs) {
        this.activePlaylistSongs = activePlaylistSongs;
    }
    public String getActivePlaylistName() {
        return activePlaylistName;
    }
    public void setActivePlaylistName(String activePlaylistName) {
        this.activePlaylistName = activePlaylistName;
    }
    public void setActivePlaylist(Playlist activePlaylist) {
        this.activePlaylist = activePlaylist;
    }
    public void setActiveSong(int activeSong) {
        this.activeSong = activeSong;
    }
    public Playlist getActivePlaylist() {
        return activePlaylist;
    }
    public int getActiveSong() {
        return activeSong;
    }
    public List<MediaItem> getActivePlaylistSongs() {
        return activePlaylistSongs;
    }
}
