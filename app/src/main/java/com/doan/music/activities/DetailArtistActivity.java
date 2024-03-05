package com.doan.music.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.doan.music.R;
import com.doan.music.models.ArtistModel;
import com.doan.music.models.ModelManager;
import com.doan.music.models.MusicModel;

import java.util.ArrayList;

public class DetailArtistActivity extends AppCompatActivity {

    private ListView listView;
    private ImageView ivCover;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album);

        ivCover = findViewById(R.id.ivCover);
        tvName = findViewById(R.id.tvName);
        listView = findViewById(R.id.listView);

        Intent intent = getIntent();
        int position = intent.getIntExtra("pos", 0);
        long albumId = intent.getLongExtra("artistId", 0);

        ModelManager modelManager = MainActivity.getModelManager();
        ArtistModel artistModel = modelManager.getArtistModels().get(position);
        ArrayList<MusicModel> musicModels = modelManager.getMusicFromArtist(albumId);

        tvName.setText(artistModel.getArtistName());
        Glide.with(this).load(artistModel.getAlbumArtUri()).into(ivCover);

        DetailAlbumAdapter adapter = new DetailAlbumAdapter(
                this,
                R.layout.list_song_item,
                musicModels
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            modelManager.onChanged(musicModels.get(i));
            adapter.notifyDataSetChanged();
        });
    }
}

class DetailArtistAdapter extends ArrayAdapter<MusicModel> {
    public DetailArtistAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MusicModel> musicModels) {
        super(context, resource, musicModels);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_song_item, parent, false);
        }

        MusicModel model = getItem(position);
        TextView songTitle = currentItemView.findViewById(R.id.songTitle);
        TextView songArtist = currentItemView.findViewById(R.id.songArtist);
        TextView songDuration = currentItemView.findViewById(R.id.songDuration);

        songTitle.setText(model.getTitle());
        songArtist.setText(model.getArtist());
        songDuration.setText(model.getDuration());

        if (model.isPlaying()) {
            currentItemView.setBackgroundResource(R.drawable.round_10_color);
            songTitle.setTextColor(Color.BLACK);
            songArtist.setTextColor(Color.BLACK);
            songDuration.setTextColor(Color.BLACK);
        } else {
            currentItemView.setBackgroundResource(R.drawable.round_10);
            songTitle.setTextColor(Color.WHITE);
            songArtist.setTextColor(Color.WHITE);
            songDuration.setTextColor(Color.WHITE);
        }

        return currentItemView;
    }
}