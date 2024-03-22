package com.doan.music.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.doan.music.R;
import com.doan.music.models.PlaylistModel;

import java.util.ArrayList;

public class PlaylistListViewAdapter extends ArrayAdapter<PlaylistModel> {
    public PlaylistListViewAdapter(@NonNull Context context, int resource, ArrayList<PlaylistModel> playlistModels) {
        super(context, resource, playlistModels);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_song_item, parent, false);
        }

        ((RelativeLayout) currentItemView.findViewById(R.id.adapterRootLayout)).setBackground(null);

        PlaylistModel model = getItem(position);
        TextView playlistTitle = currentItemView.findViewById(R.id.songTitle);
        TextView playlistDesc = currentItemView.findViewById(R.id.songArtist);
        ((TextView) currentItemView.findViewById(R.id.songDuration)).setText("");

        String desc = model.getDescription();
        if (desc == null || desc.isEmpty()) {
            desc = "<no description>";
            playlistDesc.setTextColor(Color.argb(50, 255, 255, 255));
        }
        if (desc.length() > 50) {
            desc = desc.substring(0, 50) + "...";
        }

        playlistTitle.setText(model.getTitle());
        playlistDesc.setText(desc);

        return currentItemView;
    }
}
