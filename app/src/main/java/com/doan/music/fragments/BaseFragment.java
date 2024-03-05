package com.doan.music.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseFragment extends Fragment {

    @NonNull
    public abstract RecyclerView getRecyclerView();
}
