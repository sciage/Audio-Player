package com.example.audiorecycle.interfaces;

public interface ActivityToFragment {
    void onAudioCompleteFragment();
    void onAudioUpdateFragment(int currentPosition);
    void onPause();
    void onResume();
    void onDestroy();
}