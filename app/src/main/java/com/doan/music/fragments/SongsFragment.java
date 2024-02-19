package com.doan.music.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doan.music.R;
import com.doan.music.adapter.MusicItemAdapter;
import com.doan.music.models.MusicModel;

import java.util.ArrayList;

public class SongsFragment extends Fragment {

    private static final String ARG_MUSIC_LIST = "MUSIC_LIST";

    private ArrayList<MusicModel> musicModels;
    private MusicItemAdapter musicAdapter;

    private RecyclerView musicRecyclerView;

    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment newInstance(ArrayList<MusicModel> musicModels) {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_MUSIC_LIST, musicModels);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            musicModels = getArguments().getParcelableArrayList(ARG_MUSIC_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            musicModels = requireArguments().getParcelableArrayList(ARG_MUSIC_LIST);
        }

        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        musicRecyclerView = view.findViewById(R.id.musicRecyclerView);
        musicRecyclerView.setHasFixedSize(true);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        musicAdapter = new MusicItemAdapter(this.getContext(), musicModels);
        musicRecyclerView.setAdapter(musicAdapter);
        return view;
    }
}