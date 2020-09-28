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

public class MainActivity extends AppCompatActivity implements HostInterface{


    private static final String TAG = "MainActivity";
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

}
