package com.doan.music.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.doan.music.MainPlayer;
import com.doan.music.R;
import com.doan.music.adapter.PlaylistAdapter;
import com.doan.music.adapter.PlaylistListViewAdapter;
import com.doan.music.enums.PlaybackMode;
import com.doan.music.models.ModelManager;
import com.doan.music.models.MusicModel;
import com.doan.music.models.PlaylistModel;
import com.doan.music.utils.CustomAnimationUtils;
import com.doan.music.utils.General;
import com.doan.music.utils.OnSwipeTouchListener;


public class MainPlayerView {

    private final Context context;
    private final ModelManager modelManager;
    private final TextView startTime, endTime, tvTitle, tvArtist;
    private final ImageView playPauseBtn, loopBtn, shuffleBtn, coverArt;
    private final SeekBar seekBar;

    private boolean pauseTimeUpdate;

    @SuppressLint("ClickableViewAccessibility")
    public MainPlayerView(Context context, RelativeLayout rootLayout, ModelManager modelManager) {
        this.context = context;
        this.modelManager = modelManager;
        MainPlayer mainPlayer = modelManager.getMainPlayer();

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

        int last_index = modelManager.getCurrentSongIndex();
        if (last_index >= 0) {
            setPlay(modelManager.getCurrentSong());
            endTime.setText(General.convertTimeToString(modelManager.getCurrentSong().getDuration()));
        }

        modelManager.addOnPauseButtonListener(new ModelManager.PauseButtonListener() {
            @Override
            public void onPause() {
                CustomAnimationUtils.startAnimation(playPauseBtn, context, R.anim.fadein, R.drawable.pause_icon);
            }

            @Override
            public void onPlay() {
                CustomAnimationUtils.startAnimation(playPauseBtn, context, R.anim.fadein, R.drawable.play_icon);
            }
        });

        modelManager.setOnCurrentTimeUpdateListener((currentTime, currentPercent) -> {
            if (!pauseTimeUpdate) {
                seekBar.setProgress(currentPercent);
                startTime.setText(General.convertTimeToString(currentTime));
            }
        });

        LinearLayout cardViewHolder = rootLayout.findViewById(R.id.cardViewHolder);
        CardView coverArtHolder = rootLayout.findViewById(R.id.coverArtHolder);
        cardViewHolder.setOnTouchListener(new OnSwipeTouchListener(coverArtHolder, 300) {
            @Override
            public void onSwipeListener(MotionEvent event, float x, float y) {
                coverArtHolder.setX(x);
            }

            @Override
            public void afterUpListener(float distanceX, float distanceY) {
                if (Math.abs(distanceX) < getSwipeXThreshold()) {
                    coverArtHolder.animate().translationX(0).setDuration(500);
                }
            }

            @Override
            public void onSwipeRight() {
                coverArtHolder.animate()
                        .translationX(-cardViewHolder.getWidth())
                        .setDuration(100)
                        .withEndAction(() -> {
                            coverArtHolder.setX(coverArtHolder.getWidth());
                            coverArtHolder.animate()
                                    .translationX(0)
                                    .setDuration(400);
                            modelManager.onNext(true);
                        });
            }

            @Override
            public void onSwipeLeft() {
                coverArtHolder.animate()
                        .translationX(cardViewHolder.getWidth())
                        .setDuration(100)
                        .withEndAction(() -> {
                            coverArtHolder.setX(-coverArtHolder.getWidth());
                            coverArtHolder.animate()
                                    .translationX(0)
                                    .setDuration(400);
                            modelManager.onPrev(true);
                        });
            }
        });

        playPauseCardView.setOnClickListener(view -> modelManager.onPaused());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    int currentTime = mainPlayer.getDuration() * i / 100;
                    startTime.setText(General.convertTimeToString(currentTime));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseTimeUpdate = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pauseTimeUpdate = false;
                mainPlayer.seekTo(seekBar.getProgress() * mainPlayer.getDuration() / 100);
            }
        });

        mainPlayer.setMediaOnPreparedListener(new MainPlayer.MediaPreparedListener() {
            @Override
            public void onPlay(int totalDuration) {
                endTime.setText(General.convertTimeToString(totalDuration));
            }

            @Override
            public void onPause() {
                CustomAnimationUtils.startAnimation(playPauseBtn, context, R.anim.fadein, R.drawable.pause_icon);
            }
        });

        loopBtn.setOnClickListener(view -> {
            CustomAnimationUtils.startAnimation(loopBtn, context, R.anim.fadein);
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
            CustomAnimationUtils.startAnimation(shuffleBtn, context, R.anim.fadein);
            if (mainPlayer.isShuffle()) {
                mainPlayer.setShuffle(false);
                shuffleBtn.setImageResource(R.drawable.shuffle_icon);
                return;
            }
            mainPlayer.setShuffle(true);
            shuffleBtn.setImageResource(R.drawable.shuffle_on_icon);
        });

        nextBtn.setOnClickListener(view -> modelManager.onNext(true));
        previousBtn.setOnClickListener(view -> modelManager.onPrev(true));

        ImageView library_add = rootLayout.findViewById(R.id.library_add);
        library_add.setOnClickListener(view -> {
            View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_playlist, null);
            ListView listView = view1.findViewById(R.id.listView);
            AlertDialog alertDialog = new AlertDialog.Builder(context).setView(view1).create();

            PlaylistListViewAdapter adapter = new PlaylistListViewAdapter(
                    context,
                    R.layout.list_song_item,
                    PlaylistAdapter.getPlaylistModels()
            );
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((adapterView, view2, i, l) -> {
                PlaylistModel model = PlaylistAdapter.getPlaylistModels().get(i);
                boolean result = addToPlaylist(model);
                if (!result) {
                    Toast.makeText(context, "Already in " + model.getTitle() + " playlist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Added to " + model.getTitle() + " playlist!", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            });

            alertDialog.show();
        });

        ImageView library_fav = rootLayout.findViewById(R.id.library_fav);
        library_fav.setOnClickListener(view -> {
            boolean result = addToPlaylist(PlaylistAdapter.getPlaylistModels().get(0));
            if (!result) {
                Toast.makeText(context, "Already in your favorite list", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "Added to your favorite list!", Toast.LENGTH_SHORT).show();
        });
    }

    public boolean addToPlaylist(PlaylistModel playlistModel) {
        MusicModel musicModel = modelManager.getCurrentSong();
        if (playlistModel.isExist(musicModel.getSongId())) {
            return false;
        }
        playlistModel.addItem(musicModel.getSongId());

        General.getCurrentUserRef(context)
                .child("playlists")
                .child(playlistModel.getTitle())
                .child("items")
                .setValue(playlistModel.getItems());
        return true;
    }

    public void resetTime() {
        startTime.setText(R.string.default_timestamp);
        seekBar.setProgress(0);
    }

    public void setPlay(MusicModel model) {
        ((Activity) context).runOnUiThread(() -> {
            tvTitle.setText(model.getTitle());
            tvArtist.setText(model.getArtist());
            Glide.with(context).load(model.getAlbumArtUri()).error(R.drawable.audiotrack_icon).into(coverArt);

            tvTitle.setSelected(true);
            tvArtist.setSelected(true);

            resetTime();
        });
    }

    public interface SongChangeListener {
        void onChanged(int position);

        void onChanged(MusicModel model);
    }
}
