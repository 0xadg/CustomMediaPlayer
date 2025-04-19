package com.example.custommediaplayer.handlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.example.custommediaplayer.R;
import com.example.custommediaplayer.models.PlaybackData;
import com.example.custommediaplayer.models.Playlist;
import com.example.custommediaplayer.models.Song;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {
    private static DataHandler instance = null;
    private final Context ctx;


    // Singleton-related methods and constructor
    private DataHandler(Context context)
    {
        this.ctx = context;
    }
    public static DataHandler getInstance(Context ctx)
    {
        if(instance == null)
            instance = new DataHandler(ctx);
        return instance;
    }
    public static DataHandler getInstance()
    {
        if(instance == null)
            throw new RuntimeException("Can't get instance, no context set up!");
        return instance;
    }



    public ArrayList<Playlist> getPlaylists()
    {
        // get local dir files
        // ignore one called playback
        // iterate over files w/ loadPlaylist
        // return final list
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        for(String file : ctx.fileList())
        {
            if(file.contains("playback.json") || !file.startsWith("pl_")) continue;
            playlists.add(loadPlaylist(file));
        }
        return playlists;
    }
    public PlaybackData getPlaybackData()
    {
        // test if exists
        boolean exists = false;
        for(String f : ctx.fileList())
        {
            if (f.contains("playback.json")) {
                exists = true;
                break;
            }
        }
        if(!exists) return null;
        String contents = readFile("playback.json");
        try
        {
            JSONObject obj = new JSONObject(contents);
            //if(!obj.has("activePlaylist")) throw new RuntimeException("Unnamed playlist - can't load!");

            // obtaining fields and pre-setting empty ones
            String name = obj.getString("activePlaylist");

            String activeSong = obj.getString("activeSong");
            if(activeSong.isBlank()) activeSong = "0";

            return new PlaybackData(name, null, Integer.parseInt(activeSong));
        }
        catch (Exception e)
        {
            Log.e("DATA HANDLER", "Error while loading playback data: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private String readFile(String path)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            File file = new File(ctx.getFilesDir(), path);

            FileInputStream stream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = "";

            while((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        } catch (Exception e) {
            Log.e("DATA HANDLER", "Error while reading a file: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
    private void writeFile(String path, JSONObject obj)
    {
        try
        {
            File file = new File(ctx.getFilesDir(), path);
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(obj.toString());
            output.close();
        }
        catch (Exception e)
        {
            Log.e("DATA HANDLER", "Error while writing a file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public Song loadSong(String path)
    {
        // access song with MMR
        // construct song obj w/ MMR data
        // return song
        try (MediaMetadataRetriever mmr = new MediaMetadataRetriever())
        {
            try (FileInputStream fis = new FileInputStream(path)) {
                mmr.setDataSource(fis.getFD());

                //mmr.setDataSource(ctx, path);

                // dubious, might not work?
                String mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                if (mime == null) throw new IOException("Failed to load MIME data!");
                if (!mime.contains("audio")) throw new IOException("Not an audio file!");

                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
                String prod = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
                byte[] bmp = mmr.getEmbeddedPicture();
                Bitmap cover;

                // validating everything
                if (title == null)
                    title = path.split(":")[path.split(":").length - 1];
                if (duration == null)
                    duration = "60";
                if (artist == null)
                    artist = "Unknown";
                if (year == null)
                    year = "1970";
                if (bmp == null) {
                    cover = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.image);
                } else
                    cover = BitmapFactory.decodeByteArray(bmp, 0, bmp.length);

                return new Song(title, artist, Integer.parseInt(duration) / 1000, Integer.parseInt(year), path, cover);
            }
        }
        catch (Exception e)
        {
            Log.e("DATA HANDLER", "Error while loading a song: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private Playlist loadPlaylist(String path)
    {
        String contents = readFile(path);
        if(contents.isEmpty()) throw new RuntimeException("Empty File!");
        try
        {
            JSONObject obj = new JSONObject(contents);

            // checking mandatory fields
            if(!obj.has("name")) throw new RuntimeException("Unnamed playlist - can't load!");

            // obtaining fields and pre-setting empty ones
            String name = obj.getString("name");
            if(name.isBlank()) name = "Unnamed Playlist";

            String author = obj.getString("author");
            if(author.isEmpty()) author = "Unknown";

            JSONArray songs = obj.getJSONArray("songs");
            ArrayList<Song> parsedSongs = new ArrayList<>();
            if(songs.length() > 0)
            {
                for(int i = 0; i < songs.length(); i++)
                {
                    String song = songs.getString(i);
                    parsedSongs.add(loadSong(song));
                }
            }
            return new Playlist(name, author, parsedSongs);

        }
        catch (Exception e)
        {
            Log.e("DATA HANDLER", "Error while loading a playlist: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void savePlaylist(Playlist p)
    {
        // construct JSONObject
        // put in data from obj
        // write to local path dir
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("name", p.getName());
            obj.put("author", p.getAuthor());

            JSONArray songs = new JSONArray();
            for(Song song : p.getSongs())
            {
                songs.put(song.getPath());
            }
            obj.put("songs", songs);

            writeFile("pl_" + p.getName().replace(" ", "_") + ".json", obj);
        }
        catch (Exception e)
        {
            Log.e("DATA HANDLER", "Error while saving a playlist \"" + p.getName() + "\": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public void savePlaylists(ArrayList<Playlist> p)
    {
        for(Playlist playlist : p)
        {
            savePlaylist(playlist);
        }
    }
    public void savePlaybackData(PlaybackData d)
    {
        // same as savePlaylist
        try
        {
            JSONObject obj = new JSONObject();
            obj.put("activePlaylist", d.getActivePlaylistName());
            obj.put("activeSong", d.getActiveSong());

            writeFile("playback.json", obj);
        }
        catch (Exception e)
        {
            Log.e("DATA HANDLER", "Error while saving playback data: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
