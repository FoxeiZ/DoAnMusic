package com.doan.music.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doan.music.R;
import com.doan.music.activities.MainActivity;
import com.doan.music.adapter.SongItemAdapter;
import com.doan.music.models.MusicModelManager;

public class SongsFragment extends BaseFragment {
    private MusicModelManager musicModelManager;
    private SongItemAdapter songItemAdapter;

    private RecyclerView musicRecyclerView;

    public SongsFragment() {

    }

    public SongsFragment(MusicModelManager musicModelManager) {
        this.musicModelManager = musicModelManager;
    }

/*
    public static SongsFragment newInstance(ArrayList<MusicModel> musicModels) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MUSIC_LIST, musicModels);
        fragment.setArguments(args);
        return fragment;
    }
*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        musicModelManager = mainActivity.getMusicModelManager();

        songItemAdapter = new SongItemAdapter(musicModelManager);
        musicRecyclerView.setAdapter(songItemAdapter);

        musicModelManager.subscribeToRecyclerManager("SongFragmentRV", musicRecyclerView);
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