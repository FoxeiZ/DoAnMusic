package com.doan.music.utils;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class CustomAnimationUtils {
    public static void startAnimation(ImageView imageView, Context context, int animationRes, int imageRes) {
        imageView.startAnimation(AnimationUtils.loadAnimation(
                context,
                animationRes
        ));
        imageView.setImageResource(imageRes);
    }
}
