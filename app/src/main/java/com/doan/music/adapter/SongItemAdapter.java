package com.doan.music.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.music.R;
import com.doan.music.models.ModelManager;
import com.doan.music.models.MusicModel;
import com.doan.music.utils.General;

public class SongItemAdapter extends BaseItemAdapter<SongItemAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout adapterRoot;
        public final TextView songTitle;
        public final TextView songArtist;
        public final TextView songDuration;

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


    public SongItemAdapter(ModelManager modelManager) {
        super(modelManager);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public SongItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_song_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SongItemAdapter.ViewHolder holder, int position) {
        MusicModel song = modelManager.getMusicModels().get(position);
        holder.setPlaying(song.isPlaying());

        String songDuration = song.getDuration();
        String buildDuration = General.convertTimeToString(songDuration);
        holder.songDuration.setText(buildDuration);

        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        holder.adapterRoot.setOnClickListener(view -> modelManager.onChanged(position));
    }

    @Override
    public int getItemCount() {
        return modelManager.getMusicModels().size();
    }
}
