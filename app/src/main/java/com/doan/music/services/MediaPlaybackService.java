package com.doan.music.services;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class MediaPlaybackService extends MediaSessionService {

    MediaSession mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player).build();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Player player = mediaSession.getPlayer();
        if (!player.getPlayWhenReady() || player.getMediaItemCount() == 0) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf();
        }
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
}