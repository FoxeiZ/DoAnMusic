package com.doan.music.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.doan.music.R;
import com.doan.music.models.AlbumModel;
import com.doan.music.models.ModelManager;
import com.doan.music.models.MusicModel;
import com.doan.music.utils.General;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

public class DetailAlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_album);

        ImageView ivAlbumCover = findViewById(R.id.ivCover);
        CollapsingToolbarLayout collapsed = findViewById(R.id.collapsed);
        ListView listView = findViewById(R.id.listView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();
        int position = intent.getIntExtra("pos", -1);
        long albumId = intent.getLongExtra("albumId", -1);

        if (position < 0 || albumId < 0)
            finish();

        ModelManager modelManager = MainActivity.getModelManager();
        AlbumModel albumModel = modelManager.getAlbumModels().get(position);
        ArrayList<MusicModel> musicModels = modelManager.getMusicFromAlbum(albumId);

        collapsed.setTitle(albumModel.getAlbumName());
        Glide.with(this).load(albumModel.getAlbumArtUri()).error(R.drawable.audiotrack_icon).into(ivAlbumCover);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }
}


class DetailAlbumAdapter extends ArrayAdapter<MusicModel> {
    public DetailAlbumAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MusicModel> musicModels) {
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
        songDuration.setText(General.convertTimeToString(model.getDuration()));

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
