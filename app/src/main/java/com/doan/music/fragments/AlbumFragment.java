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
import com.doan.music.adapter.AlbumItemAdapter;
import com.doan.music.models.ModelManager;

public class AlbumFragment extends BaseFragment {
    private ModelManager modelManager;

    private RecyclerView albumRecyclerView;


    public AlbumFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) requireActivity();
        modelManager = mainActivity.getModelManager();

        AlbumItemAdapter songItemAdapter = new AlbumItemAdapter(modelManager);
        albumRecyclerView.setAdapter(songItemAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albumRecyclerView = view.findViewById(R.id.musicRecyclerView);
        albumRecyclerView.setHasFixedSize(true);
        albumRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        return view;
    }

    @NonNull
    @Override
    public RecyclerView getRecyclerView() {
        return albumRecyclerView;
    }
}