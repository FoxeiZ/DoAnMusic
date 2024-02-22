package com.doan.music;

import android.media.MediaPlayer;

import com.doan.music.enums.PlaybackMode;
import com.doan.music.models.MusicModel;
import com.doan.music.models.MusicModelManager;

import java.util.ArrayList;

public class MainPlayer {

    public PlaybackMode getPlaybackMode() {
        return PLAYBACK_MODE;
    }

    public void setPlaybackMode(PlaybackMode PLAYBACK_MODE) {
        this.PLAYBACK_MODE = PLAYBACK_MODE;
    }

    private PlaybackMode PLAYBACK_MODE = PlaybackMode.PLAYBACK_MODE_NORMAL;

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    private boolean isShuffle = false;
    private final MusicModelManager musicModelManager;
    private MediaPlayer mediaPlayer;

    private MediaPreparedListener mediaPreparedListener;
    private MediaCompletionListener mediaCompletionListener;

    public interface MediaPreparedListener {
        void onPlay(int totalDuration);
        void onNotPlay();
    }

    public interface MediaCompletionListener {
        void listener();
    }

    public void setMediaOnPreparedListener(MediaPreparedListener mediaPreparedListener) {
        this.mediaPreparedListener = mediaPreparedListener;
    }

    public void setMediaCompletionListener(MediaCompletionListener mediaCompletionListener) {
        this.mediaCompletionListener = mediaCompletionListener;
    }

    public MainPlayer(MusicModelManager musicModelManager) {
        this.musicModelManager = musicModelManager;
        this.mediaPlayer = new MediaPlayer();

        // init event listener
        mediaPlayer.setOnPreparedListener(mp -> {
            if (mediaPreparedListener == null) return;

            final int getTotalDuration = mp.getDuration();
            if (!mediaPlayer.isPlaying()) {
                mediaPreparedListener.onNotPlay();
            }
            mediaPreparedListener.onPlay(getTotalDuration);
            mp.start();
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            if (mediaCompletionListener == null) return;
            mediaCompletionListener.listener();
            if (mp.isPlaying())
                next();
        });
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean play(MusicModel model) {
        return true;
    }

    public boolean play(int position) {
        MusicModel model = musicModelManager.get(position);
        return true;
    }

    public boolean play() {
        mediaPlayer.start();
        return true;
    }

    public boolean pause() {
        mediaPlayer.pause();
        return !mediaPlayer.isPlaying();
    }

    public void prev(boolean force) {
        int nextPosition = musicModelManager.getCurrentSongIndex();

        if (PLAYBACK_MODE != PlaybackMode.PLAYBACK_MODE_LOOP || force) {
            nextPosition = nextPosition -1;
        }

        if (nextPosition < 0) {
            if (PLAYBACK_MODE == PlaybackMode.PLAYBACK_MODE_LOOP_ALL) {
                nextPosition = musicModelManager.getItemCount() - 1;
            }
            else {
                nextPosition = 0;
            }
        }
        musicModelManager.onChanged(nextPosition);
    }

    public void prev() {
        prev(false);
    }

    public void next(boolean force) {
        ArrayList<MusicModel> models = musicModelManager.getCurrentModel();

        int nextPosition = musicModelManager.getCurrentSongIndex();
        if (PLAYBACK_MODE != PlaybackMode.PLAYBACK_MODE_LOOP || force) {
            nextPosition = nextPosition + 1;
        }

        if (nextPosition >= models.size()) {
            if (PLAYBACK_MODE == PlaybackMode.PLAYBACK_MODE_LOOP_ALL) {
                nextPosition = 0;
            }
            else {
                nextPosition = models.size() - 1;
            }
        }
        musicModelManager.onChanged(nextPosition);
    }

    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public int getCurrentTime() {
        return mediaPlayer.getCurrentPosition();
    }

    public void next() {
        next(false);
    }

    public void destroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
