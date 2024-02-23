package com.doan.music.views;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doan.music.R;
import com.doan.music.models.MusicModel;
import com.doan.music.models.MusicModelManager;
import com.doan.music.utils.CustomAnimationUtils;

public class MiniControlView {
    private final TextView mini_c_songTitle;
    private final ImageView mini_c_playIv;

    public MiniControlView(Context context, LinearLayout rootLayout, MusicModelManager musicModelManager) {

        LinearLayout mini_c_playBtn = rootLayout.findViewById(R.id.mini_c_playBtn);
        mini_c_playIv = rootLayout.findViewById(R.id.mini_c_playIv);
        mini_c_songTitle = rootLayout.findViewById(R.id.mini_c_songTitle);
        ImageView mini_c_nextBtn = rootLayout.findViewById(R.id.mini_c_nextBtn);

        musicModelManager.addOnPauseButtonListener(new MusicModelManager.PauseButtonListener() {
            @Override
            public void onPause() {
                CustomAnimationUtils.startAnimation(mini_c_playIv, context, R.anim.fadein, R.drawable.pause_icon);
            }

            @Override
            public void onPlay() {
                CustomAnimationUtils.startAnimation(mini_c_playIv, context, R.anim.fadein, R.drawable.play_icon);
            }
        });
        mini_c_playBtn.setOnClickListener(view -> musicModelManager.onPaused());
        mini_c_nextBtn.setOnClickListener(view -> musicModelManager.onNext(true));
    }

    public void setPlay(MusicModel model) {
        mini_c_songTitle.setText(model.getTitle());
        mini_c_songTitle.setSelected(true);
        mini_c_playIv.setImageResource(R.drawable.pause_icon);
    }
}
