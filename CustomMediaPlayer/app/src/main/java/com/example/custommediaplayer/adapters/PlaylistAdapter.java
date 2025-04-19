package com.example.custommediaplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custommediaplayer.R;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.models.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {

    private final ArrayList<Playlist> playlists;
    private ItemClickInterface clickInterface;

    public PlaylistAdapter(ArrayList<Playlist> playlists)
    {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlaylistViewHolder(v, clickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist p = playlists.get(position);

        holder.getPlaylistName().setText(p.getName());
        holder.getSongCount().setText(String.format("Songs: %d", p.getSize()));
        holder.getPlaylistAuthor().setText(p.getAuthor());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
