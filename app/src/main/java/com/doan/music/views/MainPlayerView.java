package com.doan.music.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.doan.music.MainPlayer;
import com.doan.music.R;
import com.doan.music.enums.PlaybackMode;
import com.doan.music.models.MusicModel;
import com.doan.music.models.MusicModelManager;
import com.doan.music.utils.CustomAnimationUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainPlayerView {

    private final Context context;
    private final TextView startTime, endTime, tvTitle, tvArtist;
    private final ImageView playPauseBtn, loopBtn, shuffleBtn, coverArt;
    private final SeekBar seekBar;

    public interface SongChangeListener {
        void onChanged(int position);
    }

    public MainPlayerView(Context context, RelativeLayout rootLayout, MusicModelManager musicModelManager) {
        this.context = context;
        MainPlayer mainPlayer = musicModelManager.getMainPlayer();

        rootLayout.setAlpha(0);

        ImageView nextBtn = rootLayout.findViewById(R.id.nextBtn);
        ImageView previousBtn = rootLayout.findViewById(R.id.previousBtn);
        loopBtn = rootLayout.findViewById(R.id.loopBtn);
        shuffleBtn = rootLayout.findViewById(R.id.shuffleBtn);
        seekBar = rootLayout.findViewById(R.id.customSeekBar);
        CardView playPauseCardView = rootLayout.findViewById(R.id.playPauseCardView);
        playPauseBtn = rootLayout.findViewById(R.id.playPauseBtn);
        coverArt = rootLayout.findViewById(R.id.coverArt);

        startTime = rootLayout.findViewById(R.id.currentTime);
        endTime = rootLayout.findViewById(R.id.endTime);
        tvArtist = rootLayout.findViewById(R.id.infoArtist);
        tvTitle = rootLayout.findViewById(R.id.infoTitle);

        musicModelManager.addOnPauseButtonListener(new MusicModelManager.PauseButtonListener() {
            @Override
            public void onPause() {
                CustomAnimationUtils.startAnimation(playPauseBtn, context, R.anim.fadein, R.drawable.pause_icon);
            }

            @Override
            public void onPlay() {
                CustomAnimationUtils.startAnimation(playPauseBtn, context, R.anim.fadein, R.drawable.play_icon);
            }
        });

        musicModelManager.setOnCurrentTimeUpdateListener(currentTime -> {
            seekBar.setProgress(currentTime);
            startTime.setText(convertTimeToString(currentTime));
        });

        playPauseCardView.setOnClickListener(view -> musicModelManager.onPaused());
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
            public void onPause() {
                CustomAnimationUtils.startAnimation(playPauseBtn, context, R.anim.fadein, R.drawable.pause_icon);
            }
        });

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

        nextBtn.setOnClickListener(view -> musicModelManager.onNext(true));
        previousBtn.setOnClickListener(view -> musicModelManager.onPrev(true));
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


    public void setCoverArt(Uri albumArtUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), albumArtUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getHeight(), bitmap.getHeight(), true);
            coverArt.setImageBitmap(bitmap);

        } catch (FileNotFoundException exception) {
            coverArt.setImageResource(R.drawable.audiotrack_icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setPlay(MusicModel model) {
        tvTitle.setText(model.getTitle());
        tvArtist.setText(model.getArtist());
        setCoverArt(model.getAlbumArtUri());

        tvTitle.setSelected(true);
        tvArtist.setSelected(true);

        resetBottomBar();
    }
}
