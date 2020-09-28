package com.example.audiorecycle;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.audiorecycle.audio.R;
import com.example.audiorecycle.utils.MediaPlayerUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {

    private Context context;
    private List<String> audioList = new ArrayList<>();
    private MainActivity mainActivity;
    private View view;
    private MediaPlayerUtils.Listener listener;

    public AudioListAdapter(Context context, List<String> contactList, MediaPlayerUtils.Listener listener) {
        this.context = context;
        this.audioList = contactList;
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String songPath = audioList.get(position);
        String songName = songPath.substring(songPath.lastIndexOf("/") + 1);
        holder.txtSongName.setText(songName);

        if (listener.updateList().get(position).getAudioState() == AudioStatus.AUDIO_STATE.IDLE.ordinal()
                || listener.updateList().get(position).getAudioState() == AudioStatus.AUDIO_STATE.PAUSED.ordinal()) {

            holder.btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_button));
        } else {
            holder.btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
        }
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btnPlay)
        ImageView btnPlay;

        @BindView(R.id.txtSongName)
        TextView txtSongName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    boolean ifRequest = mainActivity.requestPermissionIfNeeded();
//                    if (ifRequest) return;

                    int position = getAdapterPosition();

                    // Check if any other audio is playing
                    if (listener.updateList().get(position).getAudioState()
                            == AudioStatus.AUDIO_STATE.IDLE.ordinal()) {
                        // Reset media player
//                        MediaPlayerUtils.Listener listener = (MediaPlayerUtils.Listener) context;
                        listener.onAudioComplete();
                    }

                    String audioPath = audioList.get(position);
                    AudioStatus audioStatus = listener.updateList().get(position);
                    int currentAudioState = audioStatus.getAudioState();

                    if (currentAudioState == AudioStatus.AUDIO_STATE.PLAYING.ordinal()) {
                        // If mediaPlayer is playing, pause mediaPlayer
                        btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_button));
                        MediaPlayerUtils.pauseMediaPlayer();

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PAUSED.ordinal());
                        listener.updateList().set(position, audioStatus);
                    } else if (currentAudioState == AudioStatus.AUDIO_STATE.PAUSED.ordinal()) {
                        // If mediaPlayer is paused, play mediaPlayer
                        btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                        MediaPlayerUtils.playMediaPlayer();

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PLAYING.ordinal());
                        listener.updateList().set(position, audioStatus);
                    } else {

                        // If mediaPlayer is in idle state, start and play mediaPlayer
                        btnPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PLAYING.ordinal());
                        listener.updateList().set(position, audioStatus);

                        try {
                            MediaPlayerUtils.startAndPlayMediaPlayer(audioPath, listener, context);
                            audioStatus.setTotalDuration(MediaPlayerUtils.getTotalDuration());
                            listener.updateList().set(position, audioStatus);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    }
}
