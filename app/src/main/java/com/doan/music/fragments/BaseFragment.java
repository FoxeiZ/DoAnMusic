package com.doan.music.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.models.MusicModel;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment {

    @NonNull
    public abstract RecyclerView getRecyclerView();
}
