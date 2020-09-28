package com.example.audiorecycle.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerUtils02 {

    private static MediaPlayer mediaPlayer;
    private static MediaPlayerUtils.Listener listener;
    private static Handler mHandler;

    /**
     * Get database instance
     * @return database handler instance
     */
    public static void getInstance() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            Log.d("MUSIC", "Initialized Mediaplayer");
        } else {
            Log.d("MUSIC", "Already Initialized Mediaplayer");
        }

        if(mHandler == null) {
            mHandler = new Handler();
        }
    }

    /**
     * Release mediaPlayer
     */
    public static void releaseMediaPlayer() {
        Log.d("MUSIC", "released Mediaplayer");

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pauseMediaPlayer() {
        Log.d("MUSIC", "Paused Mediaplayer");

        mediaPlayer.pause();
    }

    public static void playMediaPlayer() {
        Log.d("MUSIC", "play Mediaplayer");

        mediaPlayer.start();
        mHandler.postDelayed(mRunnable, 100);
    }

    public static void applySeekBarValue(int selectedValue) {
        Log.d("MUSIC", "applySeekbar Mediaplayer");

        mediaPlayer.seekTo(selectedValue);
        mHandler.postDelayed(mRunnable, 100);
    }

    /**
     * Start mediaPlayer
     * @param audioUrl sd card media file
     * @throws IOException exception
     */
    public static void startAndPlayMediaPlayer(String audioUrl, final MediaPlayerUtils.Listener listener) throws IOException {
        Log.d("MUSIC", "startAndPlayMediaPlayer Mediaplayer");

        MediaPlayerUtils.listener = listener;
        getInstance();
        if(isPlaying()) {
            pauseMediaPlayer();
        }
        releaseMediaPlayer();
        try {
            getInstance();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //hideProgressBar();

                    mediaPlayer.setOnCompletionListener(onCompletionListener);

                    mHandler.postDelayed(mRunnable, 100);

                    playMediaPlayer();
                }
            });

            mediaPlayer.prepare();



//            mediaPlayer.prepare();
//            mediaPlayer.setOnCompletionListener(onCompletionListener);
//
//            mHandler.postDelayed(mRunnable, 100);
        } catch (Exception ex){
            ex.printStackTrace();
        }

//        playMediaPlayer();
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public static int getTotalDuration() {
        return mediaPlayer.getDuration();
    }

    private static MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            MediaPlayerUtils.releaseMediaPlayer();
            listener.onAudioComplete();
        }
    };

    private static Runnable mRunnable = new Runnable() {
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

}
