package com.doan.music.utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private View view;

    private final int SwipeXThreshold;
    private final int SwipeYThreshold;

    private float firstTouchX;
    private float firstTouchY;
    private final float viewX;
    private final float viewY;

    public float getViewX() {
        return viewX;
    }

    public OnSwipeTouchListener(View view, int SwipeThreshold) {
        this.view = view;
        viewX = view.getX();
        viewY = view.getY();

        this.SwipeXThreshold = SwipeThreshold;
        this.SwipeYThreshold = SwipeThreshold;
    }

    private boolean onDown(MotionEvent event) {
        firstTouchX = event.getX();
        firstTouchY = event.getY();
        return true;
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public void onSwipeUp() {
    }

    public void onSwipeDown() {
    }

    public void afterUpListener(float distanceX, float distanceY) {
    }

    private boolean onUp(MotionEvent event) {
        boolean result = false;
        float endTouchX = event.getX();
        float endTouchY = event.getY();

        float distanceX = firstTouchX - endTouchX;
        float distanceY = firstTouchY - endTouchY;

        if (Math.abs(distanceX) >= SwipeXThreshold) {
            if (distanceX > 0)
                onSwipeRight();
            else
                onSwipeLeft();
            result = true;
        }
        if (Math.abs(distanceY) >= SwipeYThreshold) {
            if (distanceY > 0)
                onSwipeUp();
            else
                onSwipeDown();
            result = true;
        }

        afterUpListener(distanceX, distanceY);
        return result;
    }

    public void onSwipeListener(MotionEvent event, float x, float y) {
    }

    private boolean onSwipe(MotionEvent event) {
        Log.d("", "onSwipe: event.getX=" + event.getX() + " event.getY=" + event.getY());
        float x = event.getX() - firstTouchX + viewX;
        float y = event.getY() - firstTouchY + viewY;
        onSwipeListener(event, x, y);
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        if (view == null) view = v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return onDown(event);
            case MotionEvent.ACTION_UP:
                return onUp(event);
            case MotionEvent.ACTION_MOVE:
                return onSwipe(event);
            default:
                break;
        }
        return false;
    }
}