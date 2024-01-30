package com.doan.music.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.doan.music.R;
import com.doan.music.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sothree.slidinguppanel.PanelSlideListener;
import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


class MainPlayer {
    private static final int PLAYBACK_MODE_NORMAL = 0;
    private static final int PLAYBACK_MODE_LOOP_ALL = 1;
    private static final int PLAYBACK_MODE_LOOP = 2;

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
            playerTimer.purge();
            playerTimer.cancel();

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


public class MainActivity extends AppCompatActivity {

    private SlidingUpPanelLayout slidingUpPanel;
    private LinearLayout miniControlLayout;
    private RelativeLayout mainPlayerLayout;

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
                if (v == 1f) {
                    miniControlLayout.setVisibility(View.GONE);
                    slidingUpPanel.setDragView(R.id.holdSlide);
                } else if (v > 0f) {
                    if (miniControlLayout.getVisibility() == View.GONE) {
                        miniControlLayout.setVisibility(View.VISIBLE);
                        slidingUpPanel.setDragView(R.id.miniControl);
                    }
                }
            }

            @Override
            public void onPanelStateChanged(@NonNull View view, @NonNull PanelState panelState, @NonNull PanelState panelState1) {
            }
        });

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
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