package com.doan.music.models;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.MainPlayer;
import com.doan.music.activities.MainActivity;
import com.doan.music.views.MainPlayerView;
import com.doan.music.adapter.BaseItemAdapter;

public class MusicModelManager implements MainPlayerView.SongChangeListener {
    private final ArrayList<MusicModel> originModel;
    private final HashMap<String, RecyclerView> recyclerViews = new HashMap<>();

    public Context getContext() {
        return context;
    }

    private final Context context;
    private MusicModel currentSong;
    private final MainPlayer mainPlayer;
    private Timer playerTimer;

    private final ArrayList<PauseButtonListener> pauseButtonListeners = new ArrayList<>();
    private final ArrayList<NextButtonListener> nextButtonListeners = new ArrayList<>();
    private final ArrayList<PrevButtonListener> prevButtonListeners = new ArrayList<>();
    private CurrentTimeUpdateListener currentTimeUpdateListener;

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
        void onUpdate(int currentTime);
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

    public MusicModelManager(ArrayList<MusicModel> models, Context context) {
        this.originModel = models;
        this.context = context;
        currentSong = models.get(0);

        mainPlayer = new MainPlayer(this);
        mainPlayer.setMediaCompletionListener(this::stopPlayerTimer);
    }

    public int getCurrentSongIndex() {
        return originModel.indexOf(currentSong);
    }

    public void subscribeToRecyclerManager(String rvName, RecyclerView recyclerView) {
        if (recyclerView == null || rvName == null) return;
        recyclerViews.put(rvName, recyclerView);
    }

    private void notifyChangeForRV(int position) {
        for (RecyclerView rv: recyclerViews.values()) {
            BaseItemAdapter adapter = (BaseItemAdapter) rv.getAdapter();
            assert adapter != null;
            adapter.notifyItemChanged(position);
        }
    }

    public MusicModel get(int position) {
        return originModel.get(position);
    }

    public ArrayList<MusicModel> getOriginModel() {
        return originModel;
    }

    public ArrayList<MusicModel> getCurrentModel() {
        return originModel;
    }


    public void startNewTimer() {
        playerTimer = new Timer();
        playerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(() -> {
                    int currentTime = mainPlayer.getCurrentTime();
                    currentTimeUpdateListener.onUpdate(currentTime);
                });
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
        int oldPosition = originModel.indexOf(currentSong);
        if (oldPosition == position) {
            mainPlayer.seekTo(0);
            mainPlayer.play();
            startNewTimer();
            return;
        }
        currentSong.setPlaying(false);
        // set new prop
        currentSong = originModel.get(position);
        currentSong.setPlaying(true);
        // notify to RecyclerView
        notifyChangeForRV(oldPosition);
        notifyChangeForRV(position);

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

    public int getItemCount() {
        return originModel.size();
    }

    public void destroy() {
        stopPlayerTimer();
        mainPlayer.destroy();
    }
}
