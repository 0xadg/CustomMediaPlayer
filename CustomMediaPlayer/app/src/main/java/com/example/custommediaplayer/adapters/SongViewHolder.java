package com.example.custommediaplayer.adapters;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custommediaplayer.R;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.interfaces.ItemType;

public class SongViewHolder extends RecyclerView.ViewHolder {

    private final TextView songTitle;
    private final TextView songArtistAndYear;
    private final TextView songDuration;
    private final ImageView songCover;
    public SongViewHolder(@NonNull View itemView, ItemClickInterface clickInterface) {
        super(itemView);
        songTitle = itemView.findViewById(R.id.songTitle);
        songArtistAndYear = itemView.findViewById(R.id.songArtist);
        songDuration = itemView.findViewById(R.id.songDuration);
        songCover = itemView.findViewById(R.id.songCover);

        // handler for regular clicks
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickInterface != null)
                {
                    SongAdapter ad = (SongAdapter) getBindingAdapter();
                    int pos = getBindingAdapterPosition();

                    if(pos != RecyclerView.NO_POSITION && ad != null) {

                        ad.updateSelection(pos);
                        clickInterface.onItemClick(pos, ItemType.Song);
                    }
                }
            }
        });

        // context menu handler
        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                if(clickInterface != null)
                {
                    int pos = getBindingAdapterPosition();
                    clickInterface.onItemContextMenuCreation(pos, ItemType.Song, contextMenu, view, contextMenuInfo);
                }
            }
        });


    }

    public TextView getSongTitle() {
        return songTitle;
    }

    public TextView getSongArtistAndYear() {
        return songArtistAndYear;
    }

    public TextView getSongDuration() {
        return songDuration;
    }

    public ImageView getSongCover() {
        return songCover;
    }
}
