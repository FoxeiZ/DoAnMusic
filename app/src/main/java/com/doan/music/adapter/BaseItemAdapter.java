package com.doan.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.MainPlayer;
import com.doan.music.models.MusicModel;
import com.doan.music.models.MusicModelManager;

import java.util.ArrayList;

public abstract class BaseItemAdapter extends RecyclerView.Adapter<SongItemAdapter.ViewHolder> {
    final MusicModelManager musicModelManager;
    final MainPlayer.SongChangeListener songChangeListener;

    BaseItemAdapter(MusicModelManager musicModelManager) {
        this.musicModelManager = musicModelManager;
        this.songChangeListener = (MainPlayer.SongChangeListener) musicModelManager;
    }
}
