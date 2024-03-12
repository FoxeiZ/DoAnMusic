package com.doan.music.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.R;
import com.doan.music.activities.MainActivity;
import com.doan.music.adapter.ArtistItemAdapter;
import com.doan.music.models.ModelManager;

public class ArtistsFragment extends BaseFragment {
    private RecyclerView artistRecyclerView;

    public ArtistsFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ModelManager modelManager = MainActivity.getModelManager();

        ArtistItemAdapter songItemAdapter = new ArtistItemAdapter(modelManager);
        artistRecyclerView.setAdapter(songItemAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        artistRecyclerView = view.findViewById(R.id.musicRecyclerView);
        artistRecyclerView.setHasFixedSize(true);
        artistRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        return view;
    }

    @NonNull
    @Override
    public RecyclerView getRecyclerView() {
        return artistRecyclerView;
    }
}