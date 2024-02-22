package com.doan.music.views;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.doan.music.MainPlayer;
import com.doan.music.R;
import com.doan.music.enums.PlaybackMode;
import com.doan.music.models.MusicModelManager;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainPlayerView {
    private MusicModelManager musicModelManager;
    private Context context;
    private RelativeLayout rootLayout;

    private MainPlayer mainPlayer;
    private TextView startTime, endTime;
    private ImageView playPauseBtn, loopBtn, shuffleBtn;
    private SeekBar seekBar;

    private Timer playerTimer;

    public boolean isPlaying() {
        return mainPlayer.isPlaying();
    }

    public interface SongChangeListener {
        void onChanged(int position);
    }

    public MainPlayer getMainPlayer() {
        return mainPlayer;
    }

    public MainPlayerView(Context context, RelativeLayout rootLayout, MusicModelManager musicModelManager) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.musicModelManager = musicModelManager;

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

        mainPlayer = new MainPlayer(musicModelManager);

        playPauseCardView.setOnClickListener(view -> onPauseButton());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mainPlayer.seekTo(i);
                    int currentTime = mainPlayer.getCurrentTime();
                    startTime.setText(convertTimeToString(currentTime));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mainPlayer.setMediaOnPreparedListener(new MainPlayer.MediaPreparedListener() {
            @Override
            public void onPlay(int totalDuration) {
                endTime.setText(convertTimeToString(totalDuration));
                seekBar.setMax(totalDuration);
            }

            @Override
            public void onNotPlay() {
                playPauseBtn.animate();
                playPauseBtn.setImageResource(R.drawable.pause_icon);
            }
        });

        mainPlayer.setMediaCompletionListener(this::stopPlayerTimer);

        loopBtn.setOnClickListener(view -> {
            loopBtn.startAnimation(AnimationUtils.loadAnimation(
                    context,
                    R.anim.fadein
            ));

            switch (mainPlayer.getPlaybackMode()) {
                case PLAYBACK_MODE_NORMAL:
                    mainPlayer.setPlaybackMode(PlaybackMode.PLAYBACK_MODE_LOOP_ALL);
                    loopBtn.setImageResource(R.drawable.loop_all_on_icon);
                    break;
                case PLAYBACK_MODE_LOOP_ALL:
                    mainPlayer.setPlaybackMode(PlaybackMode.PLAYBACK_MODE_LOOP);
                    loopBtn.setImageResource(R.drawable.loop_one_on_icon);
                    break;
                case PLAYBACK_MODE_LOOP:
                    mainPlayer.setPlaybackMode(PlaybackMode.PLAYBACK_MODE_NORMAL);
                    loopBtn.setImageResource(R.drawable.loop_all_icon);
                    break;
                default:
                    break;
            }
        });

        shuffleBtn.setOnClickListener(view -> {
            shuffleBtn.startAnimation(AnimationUtils.loadAnimation(
                    context,
                    R.anim.fadein
            ));
            if (mainPlayer.isShuffle()) {
                mainPlayer.setShuffle(false);
                shuffleBtn.setImageResource(R.drawable.shuffle_icon);
                return;
            }
            mainPlayer.setShuffle(true);
            shuffleBtn.setImageResource(R.drawable.shuffle_on_icon);
        });

        nextBtn.setOnClickListener(view -> mainPlayer.next(true));
        previousBtn.setOnClickListener(view -> mainPlayer.prev(true));
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
                ((Activity) context).runOnUiThread(() -> {
                    final int getCurrentDuration = mainPlayer.getCurrentTime();
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

        if (mainPlayer.isPlaying()) {
            stopPlayerTimer();
            mainPlayer.pause();
            playPauseBtn.setImageResource(R.drawable.play_icon);
        } else {
            startNewTimer();
            mainPlayer.play();
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
        mainPlayer.destroy();
    }
}
