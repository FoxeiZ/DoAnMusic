package com.doan.music.models;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.activities.MainActivity;
import com.doan.music.views.MainPlayerView;
import com.doan.music.adapter.BaseItemAdapter;

public class MusicModelManager implements MainPlayerView.SongChangeListener {
    private final ArrayList<MusicModel> originModel;
    private final HashMap<String, RecyclerView> recyclerViews = new HashMap<>();
    private final Context context;
    private MusicModel currentSong;

    public MusicModelManager(ArrayList<MusicModel> models, Context context) {
        this.originModel = models;
        this.context = context;
        currentSong = models.get(0);
    }

    public MusicModel getCurrentSong() {
        return currentSong;
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

    public void setCurrentSong(MusicModel currentSong) {
        this.currentSong.setPlaying(false);
        this.currentSong = currentSong;
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

    @Override
    public void onChanged(int position) {
        // change prop for old song
        int oldPosition = originModel.indexOf(currentSong);
        currentSong.setPlaying(false);
        // set new prop
        currentSong = originModel.get(position);
        currentSong.setPlaying(true);
        // notify to RecyclerView
        notifyChangeForRV(oldPosition);
        notifyChangeForRV(position);

        MainPlayerView mainPlayerView = ((MainActivity) context).getMainPlayerView();
        mainPlayerView.getMainPlayer().play(currentSong);
    }

    public int getItemCount() {
        return originModel.size();
    }
}
