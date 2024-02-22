package com.doan.music.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.doan.music.views.MainPlayerView;
import com.doan.music.R;
import com.doan.music.adapter.ViewPagerAdapter;
import com.doan.music.models.MusicModel;
import com.doan.music.models.MusicModelManager;
import com.doan.music.views.MiniControlView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sothree.slidinguppanel.PanelSlideListener;
import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private SlidingUpPanelLayout slidingUpPanel;
    private LinearLayout miniControlLayout;
    private RelativeLayout mainPlayerLayout;

    private MusicModelManager musicModelManager;

    public MainPlayerView getMainPlayerView() {
        return mainPlayerView;
    }

    public MiniControlView getMiniControlView() {
        return miniControlView;
    }

    private MainPlayerView mainPlayerView;
    private MiniControlView miniControlView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingUpPanel = findViewById(R.id.slidingUpPanel);
        ViewPager2 content = findViewById(R.id.content);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        mainPlayerLayout = findViewById(R.id.mainPlayer);
        miniControlLayout = findViewById(R.id.miniControl);

        slidingUpPanel.setDragView(R.id.miniControl);
        slidingUpPanel.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(@NonNull View view, float v) {
                miniControlLayout.setAlpha((-v+1));
                miniControlLayout.setTranslationY(32*v);
                mainPlayerLayout.setAlpha(Math.min(v*2, 1f));
            }

            @Override
            public void onPanelStateChanged(@NonNull View view, @NonNull PanelState beforeState, @NonNull PanelState afterState) {
                Log.d("",String.format(
                        "onPanelStateChanged: beforeState=%s, afterState=%s",
                        beforeState.name(),
                        afterState.name()
                ));

                if (beforeState == PanelState.DRAGGING) {
                    if (afterState == PanelState.EXPANDED) {
                        miniControlLayout.setVisibility(View.GONE);
                        slidingUpPanel.setDragView(R.id.holdSlide);
                    } else if (afterState == PanelState.COLLAPSED) {
                        slidingUpPanel.setDragView(R.id.miniControl);
                    }
                } else if (beforeState == PanelState.EXPANDED && afterState == PanelState.DRAGGING) {
                    miniControlLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // permissions check, init music db
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            somethingMusicList();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, musicModelManager);
        content.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, content, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Songs");
                    break;
                case 1:
                    tab.setText("Playlists");
                    break;
                case 2:
                    tab.setText("Albums");
                    break;
                case 3:
                    tab.setText("Artists");
                    break;
                case 4:
                    tab.setText("Genres");
                    break;
            }
        }).attach();

        mainPlayerView = new MainPlayerView(this, mainPlayerLayout, musicModelManager);
        miniControlView = new MiniControlView(this, miniControlLayout, mainPlayerView, musicModelManager);
        // Done layout init
    }

    private ArrayList<MusicModel> createMusicList() {
        ArrayList<MusicModel> musicModels = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.moveToFirst()) {
                do {
                    long cursorId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));

                    String songDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    if (songDuration == null) {
                        continue;
                    }

                    musicModels.add(new MusicModel(title, artist, songDuration, false, cursorId, albumId));
                } while (cursor.moveToNext());
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }

        return musicModels;
    }

    public MusicModelManager getMusicModelManager() {
        return musicModelManager;
    }

    public void setMusicModelManager(MusicModelManager musicModelManager) {
        this.musicModelManager = musicModelManager;
    }

    private void somethingMusicList() {
        musicModelManager = new MusicModelManager(createMusicList(), MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            somethingMusicList();
        } else {
            Toast.makeText(this, "Permission Denied. Exiting...", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanel.getPanelState() != PanelState.COLLAPSED) {
            slidingUpPanel.setPanelState(PanelState.COLLAPSED);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPlayerView.destroy();
    }
}