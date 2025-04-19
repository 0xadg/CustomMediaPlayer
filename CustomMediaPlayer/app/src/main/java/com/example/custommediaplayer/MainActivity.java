package com.example.custommediaplayer;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.media3.session.legacy.PlaybackStateCompat;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.custommediaplayer.adapters.SongAdapter;
import com.example.custommediaplayer.handlers.DataHandler;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.interfaces.ItemClickInterface;
import com.example.custommediaplayer.interfaces.ItemType;
import com.example.custommediaplayer.models.PlaybackData;
import com.example.custommediaplayer.models.Playlist;
import com.example.custommediaplayer.models.Song;
import com.example.custommediaplayer.services.PlaybackService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements ItemClickInterface {

    // Core classes
    private DataHandler dataHandler = DataHandler.getInstance(this);
    private PlaybackData playbackData;
    private ArrayList<Playlist> playlists;

    // Activity classes
    private SongAdapter adapter;
    private RecyclerView songView;
    private PlayerView playerView;
    private CardView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            playlists =     dataHandler.getPlaylists();
            playbackData =  dataHandler.getPlaybackData();
        }
        catch (Exception e) {
            playlists = new ArrayList<>();
            playbackData = null;
            Toast.makeText(this, "Couldn't load file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        testMethod();
        selectActivePlaylist();

        // add-a-song logic
        Intent intent = getIntent();
        String action = intent.getAction();
        if(Intent.ACTION_VIEW.equals(action))
        {
            Uri path = intent.getData();
            addNewSong(getRealPathFromURI(path));
        }

        prepareActiveSongs();

        // preparing service
        SessionToken token = new SessionToken(this, new ComponentName(this, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(this, token).buildAsync();

        // setting up activity
        adapter = new SongAdapter(playbackData.getActivePlaylist(), this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songView = findViewById(R.id.songView);
        songView.setLayoutManager(new LinearLayoutManager(this));
        songView.setHasFixedSize(true);
        songView.setAdapter(adapter);

        emptyMessage = findViewById(R.id.emptyMessage);
        if(adapter.getItemCount() == 0)
        {
            emptyMessage.setVisibility(VISIBLE);
        }
        else
            emptyMessage.setVisibility(GONE);

        playerView = findViewById(R.id.playerView);
        controllerFuture.addListener(() -> {
            try {
                playerView.setPlayer(controllerFuture.get());
                Player player = ((Player)controllerFuture.get());
                // handling the selection in the recyclerview
                player.addListener(new Player.Listener(){
                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                        adapter.updateSelection(playerView.getPlayer().getCurrentMediaItemIndex());
                    }

                });
                player.addMediaItems(playbackData.getActivePlaylistSongs());
                player.seekTo(playbackData.getActiveSong(), 0);
                player.prepare();
            }
            catch (Exception e)
            {
                Log.w("Player", "Something broke when trying to set up media player");
            }

        }, MoreExecutors.directExecutor());
    }

    private void selectActivePlaylist()
    {
        if (playlists.isEmpty()) {
            playlists.add(new Playlist("default", "none", new ArrayList<>()));
        }
        if(playbackData == null)
        {
            playbackData = new PlaybackData(playlists.get(0).getName(), playlists.get(0),0);
        }
        Playlist activePlaylist = playlists
                .stream()
                .filter(x -> x.getName().equals(playbackData.getActivePlaylistName()))
                .collect(Collectors.toList())
                .get(0);
        if(activePlaylist == null) {
            if (!playlists.isEmpty()) {
                playbackData.setActivePlaylist(playlists.get(0));
                playbackData.setActiveSong(0);
            }
            else
                playlists.add(new Playlist("default", "none", new ArrayList<>()));
        }
        else
            playbackData.setActivePlaylist(activePlaylist);
    }

    private void prepareActiveSongs()
    {
        ArrayList<MediaItem> preppedSongs = new ArrayList<>();
        for(Song s : playbackData.getActivePlaylist().getSongs())
        {
            preppedSongs.add(MediaItem.fromUri(s.getPath()));
        }
        playbackData.setActivePlaylistSongs(preppedSongs);
    }

    private void addNewSong(String path)
    {
        try
        {
            Song s = dataHandler.loadSong(path);
            playbackData.getActivePlaylist().addSong(s);
            if(adapter != null)
            {
                adapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        {
            Log.e("MAINACT", "Couldn't add a new song: " + e.getMessage());
            Toast.makeText(this, "Couldn't load file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void swapSongs(int pos1, int pos2)
    {
        playbackData.getActivePlaylist().swapSongs(pos1, pos2);
        if(adapter!= null)
            adapter.notifyDataSetChanged();
        List<MediaItem> songs = playbackData.getActivePlaylistSongs();
        Collections.swap(songs, pos1, pos2);
        playbackData.setActivePlaylistSongs(
            songs
        );
        adapter.setSelectedSong(pos1);
        playerView.getPlayer().moveMediaItem(pos1, pos2);
    }
    private void deleteSong(int pos)
    {
        playbackData.getActivePlaylist().removeSong(pos);
        if(adapter!= null)
            adapter.notifyDataSetChanged();
        List<MediaItem> songs = playbackData.getActivePlaylistSongs();
        songs.remove(pos);
        playbackData.setActivePlaylistSongs(
                songs
        );
        playerView.getPlayer().removeMediaItem(pos);
        adapter.setSelectedSong(-1);
    }
    private void changePlaylists()
    {

    }

    // sets up some basic data
    // TODO: REMOVE THIS WHEN DONE
    private void testMethod()
    {

    }

    // converts URI to path
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playbackData.setActiveSong(playerView.getPlayer().getCurrentMediaItemIndex());
        dataHandler.savePlaybackData(playbackData);
        dataHandler.savePlaylists(playlists);
    }

    @Override
    public void onItemClick(int position, ItemType type) {
        playbackData.setActiveSong(position);
        playerView.getPlayer().seekTo(position, 0);
        playerView.getPlayer().play();
    }

    @Override
    public void onItemContextMenuCreation(int position, ItemType type, ContextMenu menu, View v, ContextMenu.ContextMenuInfo info)
    {
        // manually constructing menus for each itemtype
        if(type.equals(ItemType.Song))
        {
            menu.setHeaderTitle("Actions");
            menu.add(1, position, 0, "Move up");
            menu.add(1, position, 0, "Move down");
            menu.add(1, position, 0, "Delete");
        }
        else
        {
            menu.setHeaderTitle("Actions");
            menu.add(2, 1, 0, "Rename");
            menu.add(2, 2, 0, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getGroupId() == 1)
        {
            // song logic here
            int pos = item.getItemId();
            switch (item.getTitle().toString())
            {
                case "Move up":
                    swapSongs(pos, pos-1);
                    break;
                case "Move down":
                    swapSongs(pos, pos+1);
                    break;
                case "Delete":
                    deleteSong(pos);
                    break;
                default:
                    Toast.makeText(this, "How did you get here?", Toast.LENGTH_LONG).show();
                    break;
            }

        }
        else
        {
            // playlist logic here
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchItem);
        MenuItem playlistInfo = menu.findItem(R.id.playlistInfo);
        SearchView sv = (SearchView) searchItem.getActionView();
        playlistInfo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                // TODO: add toggle logic...
                Toast.makeText(getApplicationContext() , "Playlist info go here...", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Playlist ap = playbackData.getActivePlaylist();
                for(int i = 0; i < ap.getSize(); i++)
                {
                    String title = ap.getSong(i).getTitle();
                    if(title.toLowerCase().contains(query))
                    {
                        playbackData.setActiveSong(i);
                        playerView.getPlayer().seekTo(i, 0);
                        playerView.getPlayer().play();
                        break;
                    }
                }
                return false;
            }

            // calling search method whenever text changes
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
}