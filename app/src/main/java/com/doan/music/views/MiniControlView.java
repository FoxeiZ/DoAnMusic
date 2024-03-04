package com.doan.music.views;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doan.music.R;
import com.doan.music.models.ModelManager;
import com.doan.music.models.MusicModel;
import com.doan.music.utils.CustomAnimationUtils;

public class MiniControlView {
    private final Context context;
    private final TextView mini_c_songTitle;
    private final ImageView mini_c_playIv;

    public MiniControlView(Context context, LinearLayout rootLayout, ModelManager modelManager) {
        this.context = context;

        LinearLayout mini_c_playBtn = rootLayout.findViewById(R.id.mini_c_playBtn);
        mini_c_playIv = rootLayout.findViewById(R.id.mini_c_playIv);
        mini_c_songTitle = rootLayout.findViewById(R.id.mini_c_songTitle);
        ImageView mini_c_nextBtn = rootLayout.findViewById(R.id.mini_c_nextBtn);

        modelManager.addOnPauseButtonListener(new ModelManager.PauseButtonListener() {
            @Override
            public void onPause() {
                CustomAnimationUtils.startAnimation(mini_c_playIv, context, R.anim.fadein, R.drawable.pause_icon);
            }

            @Override
            public void onPlay() {
                CustomAnimationUtils.startAnimation(mini_c_playIv, context, R.anim.fadein, R.drawable.play_icon);
            }
        });
        mini_c_playBtn.setOnClickListener(view -> modelManager.onPaused());
        mini_c_nextBtn.setOnClickListener(view -> modelManager.onNext(true));
    }

    public void setPlay(MusicModel model) {
        ((Activity) context).runOnUiThread(() -> {
            mini_c_songTitle.setText(model.getTitle());
            mini_c_songTitle.setSelected(true);
            mini_c_playIv.setImageResource(R.drawable.pause_icon);
        });
    }
}
