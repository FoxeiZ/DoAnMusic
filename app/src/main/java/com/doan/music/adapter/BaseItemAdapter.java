package com.doan.music.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.models.MusicModelManager;

public abstract class BaseItemAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    final MusicModelManager musicModelManager;

    BaseItemAdapter(MusicModelManager musicModelManager) {
        this.musicModelManager = musicModelManager;
    }
}