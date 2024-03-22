package com.doan.music.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.R;
import com.doan.music.activities.MainActivity;
import com.doan.music.adapter.PlaylistAdapter;
import com.doan.music.models.ModelManager;
import com.doan.music.models.PlaylistModel;
import com.doan.music.utils.General;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PlaylistFragment extends BaseFragment {

    private RecyclerView recyclerView;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ModelManager modelManager = MainActivity.getModelManager();
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(modelManager);
        recyclerView.setAdapter(playlistAdapter);
        playlistAdapter.populateData(view.getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        recyclerView = view.findViewById(R.id.playlistRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            View view1 = getLayoutInflater().inflate(R.layout.dialog_add_playlist, null);
            AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                    .setView(view1)
                    .setTitle("Create new playlist")
                    .setPositiveButton("Create", (dialog, which) -> {
                        // Handle OK button click
                        String playlistName = ((EditText) view1.findViewById(R.id.playlist_name)).getText().toString();

                        if (playlistName.isEmpty()) {
                            ((EditText) view1.findViewById(R.id.playlist_name)).setError("Playlist name can not be empty");
                            return;
                        }

                        String playlistDescription = ((EditText) view1.findViewById(R.id.playlist_desc)).getText().toString();

                        General.getCurrentUserQuery(requireContext()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                PlaylistModel playlistModel = new PlaylistModel(playlistName, playlistDescription);
                                if (snapshot.exists()) {
                                    snapshot.getRef()
                                            .child(General.getCurrentUser(requireContext()))
                                            .child("playlists")
                                            .child(playlistName)
                                            .setValue(playlistModel);
                                }

                                ((PlaylistAdapter) Objects.requireNonNull(recyclerView.getAdapter())).addData(playlistModel);
                                recyclerView.getAdapter().notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create();

            alertDialog.show();
        });
        return view;
    }

    @NonNull
    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((PlaylistAdapter) Objects.requireNonNull(recyclerView.getAdapter())).destroy();
    }
}