package com.doan.music.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class CustomAnimationUtils {
    public static void startAnimation(View view, Context context, int animationRes) {
        view.startAnimation(AnimationUtils.loadAnimation(
                context,
                animationRes
        ));
    }

    public static void startAnimation(ImageView imageView, Context context, int animationRes, int imageRes) {
        startAnimation(imageView, context, animationRes);
        imageView.setImageResource(imageRes);
    }
}
