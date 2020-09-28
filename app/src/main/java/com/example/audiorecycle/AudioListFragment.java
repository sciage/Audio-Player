package com.example.audiorecycle;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.audiorecycle.audio.R;
import com.example.audiorecycle.interfaces.ActivityToFragment;
import com.example.audiorecycle.interfaces.HostInterface;
import com.example.audiorecycle.utils.MediaPlayerUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class AudioListFragment extends Fragment implements ActivityToFragment {

    private static final String TAG = "AudioListFragment";
    HostInterface hostInterface;

    private List<String> audioList = new ArrayList<>();
    public List<AudioStatus> audioStatusList = new ArrayList<>();
    private Parcelable state;

    RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        hostInterface = (HostInterface) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_list, container, false);
        recyclerView = view.findViewById(R.id.rvAudio);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // TODO replace below audio paths with respective SD Card location
        audioList.add("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
        audioList.add("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3");
        audioList.add("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3");
        audioList.add("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3");

        for (int i = 0; i < audioList.size(); i++) {
            audioStatusList.add(new AudioStatus(AudioStatus.AUDIO_STATE.IDLE.ordinal(), 0));
        }
        setRecyclerViewAdapter(audioList);

    }


    private void setRecyclerViewAdapter(List<String> audioList) {
        AudioListAdapter adapter = new AudioListAdapter(getActivity(), audioList);
        recyclerView.setAdapter(adapter);
    }

    public List<AudioStatus> getAudioStatusList() {
        return audioStatusList;
    }

    @Override
    public void onAudioCompleteFragment() {
        // Store its state
        state = recyclerView.getLayoutManager().onSaveInstanceState();

        audioStatusList.clear();
        for (int i = 0; i < audioList.size(); i++) {
            audioStatusList.add(new AudioStatus(AudioStatus.AUDIO_STATE.IDLE.ordinal(), 0));
        }
        setRecyclerViewAdapter(audioList);

        // Main position of RecyclerView when loaded again
        if (state != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    public void onAudioUpdateFragment(int currentPosition) {
        int playingAudioPosition = -1;
        for (int i = 0; i < audioStatusList.size(); i++) {
            AudioStatus audioStatus = audioStatusList.get(i);
            if (audioStatus.getAudioState() == AudioStatus.AUDIO_STATE.PLAYING.ordinal()) {
                playingAudioPosition = i;
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Store its state
        state = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Main position of RecyclerView when loaded again
        if (state != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaPlayerUtils.releaseMediaPlayer();
    }
}