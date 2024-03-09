package com.doan.music.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.R;
import com.doan.music.activities.MainActivity;
import com.doan.music.adapter.SongItemAdapter;
import com.doan.music.models.ModelManager;

public class SongsFragment extends BaseFragment {

    private RecyclerView musicRecyclerView;

    public SongsFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ModelManager modelManager = MainActivity.getModelManager();

        SongItemAdapter songItemAdapter = new SongItemAdapter(modelManager);
        musicRecyclerView.setAdapter(songItemAdapter);

        musicRecyclerView.scrollToPosition(Math.max(0, modelManager.getCurrentSongIndex() - 1));

        modelManager.subscribeToRecyclerManager("SongFragmentRV", musicRecyclerView);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        musicRecyclerView = view.findViewById(R.id.musicRecyclerView);
        musicRecyclerView.setHasFixedSize(true);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        return view;
    }

    @NonNull
    @Override
    public RecyclerView getRecyclerView() {
        return musicRecyclerView;
    }
}