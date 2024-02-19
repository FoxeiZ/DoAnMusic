package com.doan.music.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.doan.music.R;
import com.doan.music.SongChangeListener;
import com.doan.music.adapter.ViewPagerAdapter;
import com.doan.music.models.MusicModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sothree.slidinguppanel.PanelSlideListener;
import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


class MainPlayer {
    private static final int PLAYBACK_MODE_NORMAL = 0;
    private static final int PLAYBACK_MODE_LOOP_ALL = 1;
    private static final int PLAYBACK_MODE_LOOP = 2;

    private ArrayList<MusicModel> musicModels;
    private Context context;
    private RelativeLayout rootLayout;

    private MediaPlayer mediaPlayer;
    private TextView startTime, endTime;
    private ImageView playPauseBtn, loopBtn, shuffleBtn;
    private SeekBar seekBar;

    private Timer playerTimer;

    public boolean isPlaying() {
        return isPlaying;
    }

    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private int PLAYBACK_MODE = 0;

    public MainPlayer(Context context, RelativeLayout rootLayout) {
        this.context = context;
        this.rootLayout = rootLayout;

        rootLayout.setAlpha(0);

        ImageView nextBtn = rootLayout.findViewById(R.id.nextBtn);
        ImageView previousBtn = rootLayout.findViewById(R.id.previousBtn);
        loopBtn = rootLayout.findViewById(R.id.loopBtn);
        shuffleBtn = rootLayout.findViewById(R.id.shuffleBtn);
        seekBar = rootLayout.findViewById(R.id.customSeekBar);
        CardView playPauseCardView = rootLayout.findViewById(R.id.playPauseCardView);
        playPauseBtn = rootLayout.findViewById(R.id.playPauseBtn);

        startTime = rootLayout.findViewById(R.id.currentTime);
        endTime = rootLayout.findViewById(R.id.endTime);

        mediaPlayer = new MediaPlayer();

        playPauseCardView.setOnClickListener(view -> onPauseButton());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    int getCurrentTime = mediaPlayer.getCurrentPosition();
                    startTime.setText(convertTimeToString(getCurrentTime));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnPreparedListener(mp -> {
            final int getTotalDuration = mp.getDuration();
            if (!isPlaying) {
                playPauseBtn.animate();
                playPauseBtn.setImageResource(R.drawable.pause_icon);
            }
            isPlaying = true;

            endTime.setText(convertTimeToString(getTotalDuration));
            seekBar.setMax(getTotalDuration);
            mp.start();
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            stopPlayerTimer();
            if (isPlaying) {
                nextSong();
            }
        });

        loopBtn.setOnClickListener(view -> {
            loopBtn.startAnimation(AnimationUtils.loadAnimation(
                    context,
                    R.anim.fadein
            ));
            switch (PLAYBACK_MODE) {
                case PLAYBACK_MODE_NORMAL:
                    PLAYBACK_MODE = PLAYBACK_MODE_LOOP_ALL;
                    loopBtn.setImageResource(R.drawable.loop_all_on_icon);
                    break;
                case PLAYBACK_MODE_LOOP_ALL:
                    PLAYBACK_MODE = PLAYBACK_MODE_LOOP;
                    loopBtn.setImageResource(R.drawable.loop_one_on_icon);
                    break;
                case PLAYBACK_MODE_LOOP:
                    PLAYBACK_MODE = PLAYBACK_MODE_NORMAL;
                    loopBtn.setImageResource(R.drawable.loop_all_icon);
                    break;
            }
        });

        shuffleBtn.setOnClickListener(view -> {
            shuffleBtn.startAnimation(AnimationUtils.loadAnimation(
                    context,
                    R.anim.fadein
            ));
            if (isShuffle) {
                isShuffle = false;
                shuffleBtn.setImageResource(R.drawable.shuffle_icon);
                return;
            }
            isShuffle = true;
            shuffleBtn.setImageResource(R.drawable.shuffle_on_icon);
        });

        nextBtn.setOnClickListener(view -> nextSong(true));
        previousBtn.setOnClickListener(view -> prevSong(true));
    }

    public void setMusicModels(ArrayList<MusicModel> musicModels) {
        this.musicModels = musicModels;

    }

    public void prevSong(boolean force) {
    }

    public void prevSong() {
        prevSong(false);
    }

    public void nextSong(boolean force) {
    }

    public void nextSong() {
        nextSong(false);
    }

    public String convertTimeToString(int intTime) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(intTime),
                TimeUnit.MILLISECONDS.toSeconds(intTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(intTime)));
    }

    public void resetBottomBar() {
        startTime.setText(R.string.default_timestamp);
        seekBar.setProgress(0);
    }

    public void startNewTimer() {
        playerTimer = new Timer();
        playerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity)context).runOnUiThread(() -> {
                    final int getCurrentDuration = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(getCurrentDuration);
                    startTime.setText(convertTimeToString(getCurrentDuration));
                });
            }
        }, 1000, 1000);
    }

    public void onPauseButton() {
        playPauseBtn.startAnimation(AnimationUtils.loadAnimation(
                context,
                R.anim.fadein
        ));

        if (isPlaying) {
            isPlaying = false;
            stopPlayerTimer();
            mediaPlayer.pause();
            playPauseBtn.setImageResource(R.drawable.play_icon);
        } else {
            isPlaying = true;
            startNewTimer();
            mediaPlayer.start();
            playPauseBtn.setImageResource(R.drawable.pause_icon);
        }
    }

    public void stopPlayerTimer() {
        if (playerTimer != null) {
            playerTimer.purge();
            playerTimer.cancel();
        }
    }

    public void destroy() {
        stopPlayerTimer();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


class MiniControl {

    private Context context;
    private LinearLayout rootLayout;
    private MainPlayer mainPlayer;

    public MiniControl(Context context, LinearLayout rootLayout, MainPlayer mainPlayer) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.mainPlayer = mainPlayer;

        LinearLayout mini_c_playBtn = rootLayout.findViewById(R.id.mini_c_playBtn);
        ImageView mini_c_playIv = rootLayout.findViewById(R.id.mini_c_playIv);
        TextView mini_c_songTitle = rootLayout.findViewById(R.id.mini_c_songTitle);
        ImageView mini_c_nextBtn = rootLayout.findViewById(R.id.mini_c_nextBtn);

        mini_c_playBtn.setOnClickListener(view -> {
            mainPlayer.onPauseButton();
            mini_c_playIv.startAnimation(AnimationUtils.loadAnimation(
                    context,
                    R.anim.fadein
            ));
            if (mainPlayer.isPlaying()) {
                mini_c_playIv.setImageResource(R.drawable.pause_icon);
            }
            else {
                mini_c_playIv.setImageResource(R.drawable.play_icon);
            }
        });
        mini_c_nextBtn.setOnClickListener(view -> mainPlayer.nextSong(true));
    }
}


public class MainActivity extends AppCompatActivity implements SongChangeListener {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private SlidingUpPanelLayout slidingUpPanel;
    private LinearLayout miniControlLayout;
    private RelativeLayout mainPlayerLayout;

    private static ArrayList<MusicModel> musicModels = new ArrayList<>();

    private MainPlayer mainPlayer;
    private MiniControl miniControl;

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

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, musicModels);
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

        mainPlayer = new MainPlayer(this, mainPlayerLayout);
        new MiniControl(this, miniControlLayout, mainPlayer);
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

                    String songDuration = "0";
                    songDuration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
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

    private void somethingMusicList() {
        musicModels = createMusicList();
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
    public void onChanged(int position) {

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
        mainPlayer.destroy();
    }
}