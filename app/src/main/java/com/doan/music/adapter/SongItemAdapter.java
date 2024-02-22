package com.doan.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.R;
import com.doan.music.models.MusicModel;
import com.doan.music.models.MusicModelManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SongItemAdapter extends BaseItemAdapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout adapterRoot;
        public TextView songTitle;
        public TextView songArtist;
        public TextView songDuration;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adapterRoot = itemView.findViewById(R.id.adapterRootLayout);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtist = itemView.findViewById(R.id.songArtist);
            songDuration = itemView.findViewById(R.id.songDuration);

        }

        public void setPlaying(boolean playing) {
            if (playing) {
                adapterRoot.setBackgroundResource(R.drawable.round_10_color);
                songTitle.setTextColor(Color.BLACK);
                songArtist.setTextColor(Color.BLACK);
                songDuration.setTextColor(Color.BLACK);
            } else {
                adapterRoot.setBackgroundResource(R.drawable.round_10);
                songTitle.setTextColor(Color.WHITE);
                songArtist.setTextColor(Color.WHITE);
                songDuration.setTextColor(Color.WHITE);
            }
        }
    }


    public SongItemAdapter(MusicModelManager musicModelManager) {
        super(musicModelManager);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public SongItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_song_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SongItemAdapter.ViewHolder holder, int position) {
        MusicModel song = musicModelManager.get(position);
        holder.setPlaying(song.isPlaying());

        String songDuration = song.getDuration();
        String buildDuration = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(songDuration)),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(songDuration)) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(songDuration))));
        holder.songDuration.setText(buildDuration);

        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        holder.adapterRoot.setOnClickListener(view -> songChangeListener.onChanged(position));
    }

    @Override
    public int getItemCount() {
        return musicModelManager.getItemCount();
    }
}
