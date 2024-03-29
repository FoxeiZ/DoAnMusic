package com.doan.music.models;

import static com.doan.music.activities.MainActivity.CHANNEL_ID;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.MainPlayer;
import com.doan.music.MusicService;
import com.doan.music.R;
import com.doan.music.activities.HistoryActivity;
import com.doan.music.activities.MainActivity;
import com.doan.music.utils.General;
import com.doan.music.views.MainPlayerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ModelManager implements MainPlayerView.SongChangeListener {
    public static final int NOTIFICATION_ID = 101;

    private static final String PREF_LAST_PLAYED_INDEX = "LastPlayedIndex";

    private final ArrayList<MusicModel> musicModels = new ArrayList<>();
    private final ArrayList<AlbumModel> albumModels = new ArrayList<>();
    private final ArrayList<ArtistModel> artistModels = new ArrayList<>();

    private final HashMap<Long, ArrayList<MusicModel>> albumMap = new HashMap<>();
    private final HashMap<Long, ArrayList<MusicModel>> artistMap = new HashMap<>();

    private final HashMap<String, RecyclerView> recyclerViews = new HashMap<>();

    private final Context context;
    private final MainPlayer mainPlayer;

    private final ArrayList<PauseButtonListener> pauseButtonListeners = new ArrayList<>();
    private CurrentTimeUpdateListener currentTimeUpdateListener;

    private MusicModel currentSong;
    private Timer playerTimer;

    public ModelManager(Context context) {
        this.context = context;

        loadData();
        if (musicModels.size() > 1) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            int last_index = prefs.getInt(PREF_LAST_PLAYED_INDEX, 0);
            if (last_index > musicModels.size()) {
                last_index = 0;
            }

            currentSong = musicModels.get(last_index);
            currentSong.setPlaying(true);
        }

        mainPlayer = new MainPlayer(this);
        mainPlayer.setMediaCompletionListener(this::stopPlayerTimer);
    }

    private void albumBuilder(Cursor cursor) {
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        if (albumModels.stream().anyMatch(albumModel -> albumModel.getAlbumId() == albumId)) return;
        albumMap.put(albumId, new ArrayList<>());
        AlbumModel albumModel = new AlbumModel(albumId, albumName);
        albumModels.add(albumModel);
    }

    private void artistBuilder(Cursor cursor) {
        long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String artistName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        if (artistModels.stream().anyMatch(albumModel -> albumModel.getArtistId() == artistId))
            return;

        artistMap.put(artistId, new ArrayList<>());
        ArtistModel artistModel = new ArtistModel(artistId, albumId, artistName);
        artistModels.add(artistModel);
    }

    private void songBuilder(Cursor cursor) {
        String songDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        long songId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));

        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

        MusicModel model = new MusicModel(title, artist, songDuration, false, songId, albumId);
        musicModels.add(model);
        Objects.requireNonNull(albumMap.get(albumId)).add(model);
        Objects.requireNonNull(artistMap.get(artistId)).add(model);
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

                    artistBuilder(cursor);
                    albumBuilder(cursor);
                    songBuilder(cursor);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } else {
            Toast.makeText(this.getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
        albumModels.sort(Comparator.comparing(AlbumModel::getAlbumName));
        artistModels.sort(Comparator.comparing(ArtistModel::getArtistName));
        musicModels.sort(Comparator.comparing(MusicModel::getTitle));
    }

    public Context getContext() {
        return context;
    }

    public void addOnPauseButtonListener(PauseButtonListener pauseButtonListener) {
        pauseButtonListeners.add(pauseButtonListener);
    }

    public void setOnCurrentTimeUpdateListener(CurrentTimeUpdateListener currentTimeUpdateListener) {
        this.currentTimeUpdateListener = currentTimeUpdateListener;
    }

    public MusicModel getCurrentSong() {
        return currentSong;
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

    public ArrayList<ArtistModel> getArtistModels() {
        return artistModels;
    }

    public ArrayList<MusicModel> getMusicFromAlbum(long albumId) {
        return albumMap.get(albumId);
    }

    public ArrayList<MusicModel> getMusicFromArtist(long artistId) {
        return artistMap.get(artistId);
    }

    public void startNewTimer() {
        playerTimer = new Timer();
        playerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(() -> currentTimeUpdateListener.onUpdate(mainPlayer.getCurrentTime(), mainPlayer.getCurrentPercent()));
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_LAST_PLAYED_INDEX, position);
        editor.apply();

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
            @SuppressWarnings("rawtypes") RecyclerView.Adapter adapter = Objects.requireNonNull(recyclerView.getAdapter());
            adapter.notifyItemChanged(position);
            adapter.notifyItemChanged(oldPosition);
        });

        MainActivity mainActivity = ((MainActivity) context);
        mainActivity.getMainPlayerView().setPlay(currentSong);
        mainActivity.getMiniControlView().setPlay(currentSong);

        mainPlayer.play(currentSong);
        startNewTimer();
        sendNotification(true);

        General.getCurrentUserRef(context)
                .child("history")
                .push()
                .setValue(currentSong.getSongId())
                .addOnSuccessListener(runnable -> {
                    HistoryActivity historyInstance = HistoryActivity.getInstance();
                    if (historyInstance != null) {
                        historyInstance.addHistory(currentSong);
                    }

                });
    }

    @Override
    public void onChanged(MusicModel model) {
        onChanged(musicModels.indexOf(model));
    }

    public void onPaused() {
        if (isPlaying()) {
            stopPlayerTimer();
            mainPlayer.pause();
        } else {
            startNewTimer();
            mainPlayer.play();
        }

        sendNotification(null);
        for (PauseButtonListener listener : pauseButtonListeners) {
            if (isPlaying()) {
                listener.onPause();
            } else {
                listener.onPlay();
            }
        }
    }

    public ArrayList<MusicModel> searchFor(String query) {
        ArrayList<MusicModel> searchResult = new ArrayList<>();
        musicModels.forEach(model -> {
            if (model.isContain(query)) searchResult.add(model);
        });
        return searchResult;
    }

    public void onNext(boolean force) {
        mainPlayer.next(force);
    }

    public void onPrev(boolean force) {
        mainPlayer.prev(force);
    }

    public MainPlayer getMainPlayer() {
        return mainPlayer;
    }

    public boolean isPlaying() {
        return mainPlayer.isPlaying();
    }

    private PlaybackStateCompat playbackStateCompatBuilder() {
        PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();

        int state = isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
        long position = mainPlayer.getCurrentTime();

        long stateActions = PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PLAY_PAUSE
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SEEK_TO;

        playbackStateBuilder.setState(state, position, 1);
        playbackStateBuilder.setActions(stateActions);
        return playbackStateBuilder.build();
    }

    private Notification createNotification(boolean isPlaying) {
        mainPlayer.getMediaSession().setPlaybackState(playbackStateCompatBuilder());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.audiotrack_icon)
                .setContentTitle(currentSong.getTitle())
                .setContentText(currentSong.getArtist())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                        .setMediaSession(mainPlayer.getSessionToken())
                );

        builder.addAction(R.drawable.previous_song_button, "Previous", MusicService.pendingIntentPrev(getContext()));

        if (isPlaying) {
            builder.addAction(R.drawable.pause_icon, "Pause", MusicService.pendingIntentPlay(getContext()));
        } else {
            builder.addAction(R.drawable.play_icon, "Play", MusicService.pendingIntentPause(getContext()));
        }

        builder.addAction(R.drawable.next_song_button, "Next", MusicService.pendingIntentNext(getContext()));
        return builder.build();
    }

    private void sendNotification(@Nullable Boolean isPlaying) {
        Notification notification = createNotification(isPlaying == null ? isPlaying() : isPlaying);
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void destroy() {
        stopPlayerTimer();
        mainPlayer.destroy();
    }

    public interface PauseButtonListener {
        void onPlay();

        void onPause();
    }

    public interface CurrentTimeUpdateListener {
        void onUpdate(int currentTime, int currentPercent);
    }
}
