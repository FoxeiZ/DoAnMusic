package com.doan.music;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.doan.music.activities.MainActivity;
import com.doan.music.models.ModelManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MusicService extends IntentService {
    private static final String ACTION_PLAY = "com.doan.music.action.PLAY";
    private static final String ACTION_PAUSE = "com.doan.music.action.PAUSE";
    private static final String ACTION_NEXT = "com.doan.music.action.NEXT";
    private static final String ACTION_PREV = "com.doan.music.action.PREV";

    public MusicService() {
        super("MusicService");
    }

    public static PendingIntent pendingIntentPlay(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PLAY);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static PendingIntent pendingIntentPause(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PAUSE);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static PendingIntent pendingIntentNext(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_NEXT);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static PendingIntent pendingIntentPrev(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PREV);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                handleActionPlay();
            } else if (ACTION_PAUSE.equals(action)) {
                handleActionPause();
            } else if (ACTION_NEXT.equals(action)) {
                handleActionNext();
            } else if (ACTION_PREV.equals(action)) {
                handleActionPrev();
            }
        }
    }

    private void handleActionPlay() {
        ModelManager modelManager = MainActivity.getModelManager();
        modelManager.onPaused();
    }

    private void handleActionPause() {
        ModelManager modelManager = MainActivity.getModelManager();
        modelManager.onPaused();
    }

    private void handleActionNext() {
        ModelManager modelManager = MainActivity.getModelManager();
        modelManager.onNext(true);
    }

    private void handleActionPrev() {
        ModelManager modelManager = MainActivity.getModelManager();
        modelManager.onPrev(true);
    }
}