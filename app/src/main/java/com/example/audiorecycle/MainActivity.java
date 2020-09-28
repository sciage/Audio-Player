package com.example.audiorecycle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import com.example.audiorecycle.audio.R;
import com.example.audiorecycle.interfaces.HostInterface;
import com.example.audiorecycle.utils.MediaPlayerUtils;

public class MainActivity extends AppCompatActivity implements HostInterface, MediaPlayerUtils.Listener{


    private static final String TAG = "MainActivity";

    private boolean doubleBackToExitPressedOnce = false;

    private Parcelable state;
    private AudioListFragment audioListFragment=new AudioListFragment();
    private static final int RC_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeCurrentFragmentTo(audioListFragment);
        requestPermissionIfNeeded();

    }


    public List<AudioStatus> getAudioList(){
        return audioListFragment.getAudioStatusList();
    }

    @Override
    public void onAudioUpdate(int currentPosition) {
        audioListFragment.onAudioUpdateFragment(currentPosition);
    }

    @Override
    public void onAudioComplete() {
        audioListFragment.onAudioCompleteFragment();
    }


    @Override
    public void onPause() {
        super.onPause();
        // Store its state
        audioListFragment.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Main position of RecyclerView when loaded again
        if (state != null) {
            audioListFragment.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaPlayerUtils.releaseMediaPlayer();
    }

    @Override
    public void changeCurrentFragmentTo(Fragment fragment) {
        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.replace(R.id.fragment_container, fragment, fragment.getClass().getName());
        fTransaction.commit();
    }

    ////////////////////// Permission /////////////////////
    public boolean requestPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_PERMISSION);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    ///////////////////////////////////////////////////////

    ////////////////////Exit From App///////////////////////
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}
