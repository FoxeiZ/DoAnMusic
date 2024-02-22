package com.doan.music.views;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doan.music.R;
import com.doan.music.models.MusicModelManager;

public class MiniControlView {
    final private Context context;
    final private LinearLayout rootLayout;
    final private MusicModelManager musicModelManager;
    final private MainPlayerView mainPlayer;

    public MiniControlView(Context context, LinearLayout rootLayout, MainPlayerView mainPlayer, MusicModelManager musicModelManager) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.mainPlayer = mainPlayer;
        this.musicModelManager = musicModelManager;

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
        mini_c_nextBtn.setOnClickListener(view -> mainPlayer.getMainPlayer().next(true));
    }
}
