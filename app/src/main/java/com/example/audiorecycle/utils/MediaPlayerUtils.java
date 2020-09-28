package com.example.audiorecycle.utils;

import android.app.ProgressDialog;
import android.content.Context;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


import com.example.audiorecycle.audio.R;

import java.io.IOException;

public class MediaPlayerUtils {

    public static final String TAG = "MediaPlayerUtils";

    public static MediaPlayer mediaPlayer;
    public static MediaPlayerUtils.Listener listener;
    public static Handler mHandler;

    public static ProgressDialog progress;

    public static void getInstance() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if (mHandler == null) {
            mHandler = new Handler();
        }
    }

    public static void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pauseMediaPlayer() {
        mediaPlayer.pause();
    }

    public static void playMediaPlayer() {
        mediaPlayer.start();
        mHandler.postDelayed(mRunnable, 100);
    }


    public static void startAndPlayMediaPlayer(String audioUrl, final MediaPlayerUtils.Listener listener, Context context) throws IOException {

        MediaPlayerUtils.listener = listener;

        getInstance();
        if (isPlaying()) {
            pauseMediaPlayer();
        }
        releaseMediaPlayer();
        getInstance();

        showProgressBarWithoutHide(context);

        mediaPlayer.setDataSource(audioUrl);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                hideProgressBar();

                mediaPlayer.setOnCompletionListener(onCompletionListener);
                mHandler.postDelayed(mRunnable, 100);

                playMediaPlayer();
            }
        });
        mediaPlayer.prepareAsync();
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static int getTotalDuration() {
        return mediaPlayer.getDuration();
    }

    public static MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            MediaPlayerUtils.releaseMediaPlayer();
            listener.onAudioComplete();
        }
    };

    public static Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (isPlaying()) {
                    mHandler.postDelayed(mRunnable, 100);
                    listener.onAudioUpdate(mediaPlayer.getCurrentPosition());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public interface Listener {
        void onAudioComplete();

        void onAudioUpdate(int currentPosition);
    }


    public static void hideProgressBar() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

    }

    public static void showProgressBarWithoutHide(Context context) {
        if (progress == null) {
            progress = new ProgressDialog(context);
            progress.setMessage(context.getString(R.string.please_wait));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();
        } else if (!progress.isShowing()) {
            progress.show();
        }
    }

}
