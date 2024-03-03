package com.doan.music.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.models.ModelManager;

public abstract class BaseItemAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    final ModelManager modelManager;

    BaseItemAdapter(ModelManager modelManager) {
        this.modelManager = modelManager;
    }
}