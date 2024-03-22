package com.doan.music.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.doan.music.fragments.AlbumFragment;
import com.doan.music.fragments.ArtistsFragment;
import com.doan.music.fragments.BaseFragment;
import com.doan.music.fragments.PlaylistFragment;
import com.doan.music.fragments.SongsFragment;

import java.util.HashMap;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final HashMap<Integer, BaseFragment> hashMap = new HashMap<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        hashMap.put(0, new SongsFragment());
        hashMap.put(1, new PlaylistFragment());
    }

    private BaseFragment internalCreateFragment(int position) {
        if (hashMap.containsKey(position)) return hashMap.get(position);
        switch (position) {
            case 1:
                return new PlaylistFragment();
            case 2:
                return new AlbumFragment();
            case 3:
                return new ArtistsFragment();
            default:
                return new SongsFragment();
        }
    }

    @NonNull
    @Override
    public BaseFragment createFragment(int position) {
        BaseFragment fragment = internalCreateFragment(position);
        if (!hashMap.containsKey(position)) hashMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
