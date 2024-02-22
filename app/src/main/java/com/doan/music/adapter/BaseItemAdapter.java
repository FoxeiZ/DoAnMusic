package com.doan.music.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.views.MainPlayerView;
import com.doan.music.models.MusicModelManager;

public abstract class BaseItemAdapter extends RecyclerView.Adapter<SongItemAdapter.ViewHolder> {
    final MusicModelManager musicModelManager;
    final MainPlayerView.SongChangeListener songChangeListener;

    BaseItemAdapter(MusicModelManager musicModelManager) {
        this.musicModelManager = musicModelManager;
        this.songChangeListener = musicModelManager;
    }
}
