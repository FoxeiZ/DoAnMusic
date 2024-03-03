package com.doan.music.models;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.MainPlayer;
import com.doan.music.activities.MainActivity;
import com.doan.music.views.MainPlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ModelManager implements MainPlayerView.SongChangeListener {
    private final ArrayList<MusicModel> musicModels = new ArrayList<>();
    private final ArrayList<AlbumModel> albumModels = new ArrayList<>();
    private final HashMap<String, RecyclerView> recyclerViews = new HashMap<>();
    private final Context context;
    private final MainPlayer mainPlayer;
    private final ArrayList<PauseButtonListener> pauseButtonListeners = new ArrayList<>();
    private final ArrayList<NextButtonListener> nextButtonListeners = new ArrayList<>();
    private final ArrayList<PrevButtonListener> prevButtonListeners = new ArrayList<>();
    private MusicModel currentSong;
    private Timer playerTimer;
    private CurrentTimeUpdateListener currentTimeUpdateListener;

    public ModelManager(Context context) {
        this.context = context;

        loadData();
        currentSong = musicModels.get(1);

        mainPlayer = new MainPlayer(this);
        mainPlayer.setMediaCompletionListener(this::stopPlayerTimer);
    }

    private void albumBuilder(Cursor cursor) {
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        if (albumModels.stream().anyMatch(albumModel -> albumModel.getAlbumId() == albumId))
            return;
        albumModels.add(new AlbumModel(albumId, albumName));
    }

    private void musicBuilder(Cursor cursor) {
        String songDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        long songId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        musicModels.add(new MusicModel(title, artist, songDuration, false, songId, albumId));
    }

    private void loadData() {
        ContentResolver contentResolver = getContext().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) == null)
                        continue;

                    albumBuilder(cursor);
                    musicBuilder(cursor);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } else {
            Toast.makeText(this.getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    public Context getContext() {
        return context;
    }

    public void addOnPauseButtonListener(PauseButtonListener pauseButtonListener) {
        pauseButtonListeners.add(pauseButtonListener);
    }

    public void addOnNextButtonListener(NextButtonListener nextButtonListener) {
        nextButtonListeners.add(nextButtonListener);
    }

    public void addOnPrevButtonListener(PrevButtonListener prevButtonListener) {
        prevButtonListeners.add(prevButtonListener);
    }

    public void setOnCurrentTimeUpdateListener(CurrentTimeUpdateListener currentTimeUpdateListener) {
        this.currentTimeUpdateListener = currentTimeUpdateListener;
    }

    public int getCurrentSongIndex() {
        return musicModels.indexOf(currentSong);
    }

    public void subscribeToRecyclerManager(String rvName, RecyclerView recyclerView) {
        if (recyclerView == null || rvName == null) return;
        recyclerViews.put(rvName, recyclerView);
    }

    public ArrayList<MusicModel> getMusicModels() {
        return musicModels;
    }

    public ArrayList<AlbumModel> getAlbumModels() {
        return albumModels;
    }

    public void startNewTimer() {
        playerTimer = new Timer();
        playerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context)
                        .runOnUiThread(() -> currentTimeUpdateListener
                                .onUpdate(
                                        mainPlayer.getCurrentTime(),
                                        mainPlayer.getCurrentPercent()
                                ));
            }
        }, 1000, 1000);
    }

    public void stopPlayerTimer() {
        if (playerTimer != null) {
            playerTimer.purge();
            playerTimer.cancel();
        }
    }

    @Override
    public void onChanged(int position) {
        if (mainPlayer.isPlaying()) {
            stopPlayerTimer();
            mainPlayer.pause();
        }

        // change prop for old song
        int oldPosition = musicModels.indexOf(currentSong);
        if (oldPosition == position) {
            mainPlayer.seekTo(0);
            mainPlayer.play();
            startNewTimer();
            return;
        }
        currentSong.setPlaying(false);
        // set new prop
        currentSong = musicModels.get(position);
        currentSong.setPlaying(true);
        // notify to RecyclerView
        recyclerViews.forEach((s, recyclerView) -> {
            RecyclerView.Adapter adapter = Objects.requireNonNull(recyclerView.getAdapter());
            adapter.notifyItemChanged(position);
            adapter.notifyItemChanged(oldPosition);
        });

        MainActivity mainActivity = ((MainActivity) context);
        mainActivity.getMainPlayerView().setPlay(currentSong);
        mainActivity.getMiniControlView().setPlay(currentSong);

        mainPlayer.play(currentSong);
        startNewTimer();
    }

    public void onPaused() {
        if (isPlaying()) {
            stopPlayerTimer();
            mainPlayer.pause();
        } else {
            startNewTimer();
            mainPlayer.play();
        }

        for (PauseButtonListener listener : pauseButtonListeners) {
            if (isPlaying()) {
                listener.onPause();
            } else {
                listener.onPlay();
            }
        }
    }

    public void onNext(boolean force) {
        for (NextButtonListener listener :
                nextButtonListeners) {
            listener.onClick();
        }
        mainPlayer.next(force);
    }

    public void onPrev(boolean force) {
        for (PrevButtonListener listener :
                prevButtonListeners) {
            listener.onClick();
        }
        mainPlayer.prev(force);
    }

    public MainPlayer getMainPlayer() {
        return mainPlayer;
    }

    public boolean isPlaying() {
        return mainPlayer.isPlaying();
    }

    public void destroy() {
        stopPlayerTimer();
        mainPlayer.destroy();
    }

    public interface PauseButtonListener {
        void onPlay();

        void onPause();
    }

    public interface NextButtonListener {
        void onClick();
    }

    public interface PrevButtonListener {
        void onClick();
    }

    public interface CurrentTimeUpdateListener {
        void onUpdate(int currentTime, int currentPercent);
    }
}
