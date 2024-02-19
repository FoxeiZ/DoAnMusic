package com.doan.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.R;
import com.doan.music.SongChangeListener;
import com.doan.music.models.MusicModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicItemAdapter extends RecyclerView.Adapter<MusicItemAdapter.ViewHolder> {

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

        public void setPlaying(boolean v) {
            if (v) {
                adapterRoot.setBackgroundResource(R.drawable.round_10_color);
                songTitle.setTextColor(-16777216);
                songArtist.setTextColor(-16777216);
                songDuration.setTextColor(-16777216);
            }
            else {
                adapterRoot.setBackgroundResource(R.drawable.round_10);
                songTitle.setTextColor(0xfff2f2f2);
                songArtist.setTextColor(0xfff2f2f2);
                songDuration.setTextColor(0xfff2f2f2);
            }
        }
    }

    private ArrayList<MusicModel> musicModels;
    private final SongChangeListener songChangeListener;
    private MusicModel currentPlaying;

    public MusicItemAdapter(Context context, ArrayList<MusicModel> musicModels) {
        this.musicModels = musicModels;
        currentPlaying = musicModels.get(0);
        this.songChangeListener = (SongChangeListener) context;
    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MusicItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_song_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItemAdapter.ViewHolder holder, int position) {
        MusicModel song = musicModels.get(position);
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

    public void updateSongState(MusicModel model) {
        int idx = musicModels.indexOf(model);
        if (idx > -1) {
            notifyItemChanged(idx);
        }
    }

    public void updateSongsState(MusicModel m1, MusicModel m2) {
        updateSongState(m1);
        updateSongState(m2);
        currentPlaying = m2;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<MusicModel> list) {
        this.musicModels = list;
        notifyDataSetChanged();
    }

    public MusicModel getCurrentPlaying() {
        return currentPlaying;
    }

    @Override
    public int getItemCount() {
        return musicModels.size();
    }
}
