package com.example.custommediaplayer.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custommediaplayer.R;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.interfaces.ItemType;

public class PlaylistViewHolder extends RecyclerView.ViewHolder {

    private TextView playlistName;
    private TextView songCount;
    private TextView playlistAuthor;
    public PlaylistViewHolder(@NonNull View itemView, ItemClickInterface clickInterface) {
        super(itemView);
        playlistName = itemView.findViewById(R.id.playlistName);
        songCount = itemView.findViewById(R.id.songCount);
        playlistAuthor = itemView.findViewById(R.id.authorName);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickInterface != null)
                {
                    int pos = getBindingAdapterPosition();
                    clickInterface.onItemClick(pos, ItemType.Playlist);
                }
            }
        });
    }

    public TextView getPlaylistAuthor() {
        return playlistAuthor;
    }

    public TextView getPlaylistName() {
        return playlistName;
    }

    public TextView getSongCount() {
        return songCount;
    }
}
