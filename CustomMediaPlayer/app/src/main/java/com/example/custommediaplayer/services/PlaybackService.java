package com.example.custommediaplayer.services;


import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

// playback service
// handles media playback when the app isn't active
// and working in background
public class PlaybackService extends MediaSessionService {
    private MediaSession session = null;

    @Override
    public void onCreate()
    {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        session = new androidx.media3.session.MediaSession.Builder(this, player).build();

        player.prepare();
    }

    @Override
    public void onDestroy() {
        session.getPlayer().release();
        session.release();
        session = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return session;
    }
}
