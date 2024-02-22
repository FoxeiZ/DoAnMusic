package com.doan.music.models;

import android.content.Context;

import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.MainPlayer;
import com.doan.music.activities.MainActivity;
import com.doan.music.adapter.BaseItemAdapter;

public class MusicModelManager implements MainPlayer.SongChangeListener {
    private final ArrayList<MusicModel> originModel;
    private final ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
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

    public void subscribeToRecyclerManager(RecyclerView recyclerView) {
        if (recyclerView == null) return;
        if (!recyclerViews.contains(recyclerView))
            recyclerViews.add(recyclerView);
    }

    private void notifyChangeForRV(int position) {
        for (RecyclerView rv: recyclerViews) {
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
        int oldPosition = originModel.indexOf(currentSong);
        currentSong.setPlaying(false);
        currentSong = originModel.get(position);
        currentSong.setPlaying(true);

        notifyChangeForRV(oldPosition);
        notifyChangeForRV(position);
    }

    public int getItemCount() {
        return originModel.size();
    }
}
