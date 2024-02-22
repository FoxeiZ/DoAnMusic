package com.doan.music.views;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.doan.music.R;
import com.doan.music.models.MusicModelManager;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainPlayerView {
    private static final int PLAYBACK_MODE_NORMAL = 0;
    private static final int PLAYBACK_MODE_LOOP_ALL = 1;
    private static final int PLAYBACK_MODE_LOOP = 2;

    private MusicModelManager musicModelManager;
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

    public interface SongChangeListener {
        void onChanged(int position);
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
                ((Activity) context).runOnUiThread(() -> {
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
