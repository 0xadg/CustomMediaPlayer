package com.example.custommediaplayer.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custommediaplayer.R;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.models.Playlist;
import com.example.custommediaplayer.models.Song;

public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {
    private final Playlist playlist;
    private ItemClickInterface clickInterface;
    private int selectedSong = RecyclerView.NO_POSITION;

    public SongAdapter(Playlist playlist, ItemClickInterface clickInterface)
    {
        this.playlist = playlist;
        this.clickInterface = clickInterface;
    }


    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(v, clickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {

        Song s = playlist.getSong(position);

        if(selectedSong == position)
            holder.itemView.setBackgroundColor(Color.rgb(107, 107, 107));
        else
            holder.itemView.setBackgroundColor(Color.GRAY);

        holder.getSongTitle().setText(s.getTitle());
        holder.getSongArtistAndYear().setText(String.format("%s", s.getArtist()));
        holder.getSongDuration().setText(String.format("%d:%02d", s.getDuration() / 60, s.getDuration() % 60));
        holder.getSongCover().setImageBitmap(s.getCover());
    }

    public void setSelectedSong(int position)
    {
        selectedSong = position;
    }

    public void updateSelection(int newpos)
    {
        notifyItemChanged(getSelectedSong());
        selectedSong = newpos;
        notifyItemChanged(newpos);
    }

    public int getSelectedSong() {
        return selectedSong;
    }

    @Override
    public int getItemCount() {
        return playlist.getSize();
    }
}
