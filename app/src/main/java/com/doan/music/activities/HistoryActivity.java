package com.doan.music.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.doan.music.R;
import com.doan.music.models.ModelManager;
import com.doan.music.models.MusicModel;
import com.doan.music.utils.General;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private final ArrayList<MusicModel> musicModels = new ArrayList<>();
    public static WeakReference<HistoryActivity> historyActivityWeakReference;

    private HistoryAdapter adapter;

    public static HistoryActivity getInstance() {
        if (historyActivityWeakReference == null || historyActivityWeakReference.get() == null)
            return null;
        return historyActivityWeakReference.get();
    }

    public void addHistory(MusicModel model) {
        musicModels.add(model);
        adapter.notifyDataSetChanged();
    }

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

        ModelManager modelManager = MainActivity.getModelManager();
        collapsed.setTitle("History");
        Glide.with(this).load(R.drawable.history_icon).into(ivAlbumCover);

        adapter = new HistoryAdapter(
                this,
                R.layout.list_song_item,
                musicModels
        );
        listView.setAdapter(adapter);

        General.getCurrentUserRef(this)
                .child("history")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(child -> {
                            Long musicId = child.getValue(Long.class);
                            if (musicId == null)
                                return;

                            modelManager
                                    .getMusicModels()
                                    .stream()
                                    .filter(model -> model.getSongId() == musicId)
                                    .findFirst().ifPresent(musicModels::add);
                        });
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("", "onCancelled: " + error.getMessage());
                    }
                });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            modelManager.onChanged(musicModels.get(musicModels.size() - 1 - i));
            adapter.notifyDataSetChanged();
        });

        historyActivityWeakReference = new WeakReference<>(this);
    }
}


class HistoryAdapter extends ArrayAdapter<MusicModel> {
    public HistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MusicModel> musicModels) {
        super(context, resource, musicModels);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_song_item, parent, false);
        }

        MusicModel model = getItem(getCount() - 1 - position);
        RelativeLayout rootLayout = currentItemView.findViewById(R.id.adapterRootLayout);
        TextView songTitle = currentItemView.findViewById(R.id.songTitle);
        TextView songArtist = currentItemView.findViewById(R.id.songArtist);
        TextView songDuration = currentItemView.findViewById(R.id.songDuration);

        songTitle.setText(model.getTitle());
        songArtist.setText(model.getArtist());
        songDuration.setText(General.convertTimeToString(model.getDuration()));

        if (model.isPlaying()) {
            rootLayout.setBackgroundResource(R.drawable.round_10_color);
            songTitle.setTextColor(Color.BLACK);
            songArtist.setTextColor(Color.BLACK);
            songDuration.setTextColor(Color.BLACK);
        } else {
            rootLayout.setBackgroundResource(R.drawable.round_10);
            songTitle.setTextColor(Color.WHITE);
            songArtist.setTextColor(Color.WHITE);
            songDuration.setTextColor(Color.WHITE);
        }

        return currentItemView;
    }
}