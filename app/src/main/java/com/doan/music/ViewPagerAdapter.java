package com.doan.music;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.doan.music.fragments.AlbumFragment;
import com.doan.music.fragments.ArtistsFragment;
import com.doan.music.fragments.GenresFragment;
import com.doan.music.fragments.PlaylistFragment;
import com.doan.music.fragments.SongsFragment;

import java.util.HashMap;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final HashMap<Integer, Fragment> hashMap = new HashMap<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    private Fragment internalCreateFragment(int position) {
        if (hashMap.containsKey(position)) return hashMap.get(position);
        switch (position) {
            case 1:
                return new PlaylistFragment();
            case 2:
                return new AlbumFragment();
            case 3:
                return new ArtistsFragment();
            case 4:
                return new GenresFragment();
            default:
                return new SongsFragment();
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = internalCreateFragment(position);
        if (!hashMap.containsKey(position)) hashMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}