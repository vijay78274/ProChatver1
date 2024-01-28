package com.example.prochatver1.Acitivities;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prochatver1.R;

import java.io.IOException;
import java.util.Locale;

public class AudioPlay extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private String firebaseAudioUrl;
    private SeekBar seekBar;
    private TextView durationTextView;
    private ImageView playPauseButton;
    private Handler handler;
    private boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);
        firebaseAudioUrl = getIntent().getStringExtra("documentUrl");
        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.seekBar);
        playPauseButton = findViewById(R.id.playPauseButton);
        durationTextView = findViewById(R.id.durationTextView);
        handler = new Handler(Looper.getMainLooper());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        // Check for Android version to set audio attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createMediaPlayerWithAttributes();
        } else {
            createMediaPlayer();
        }

        // Set data source and prepare the MediaPlayer
        try {
            mediaPlayer.setDataSource(firebaseAudioUrl);
            mediaPlayer.prepareAsync(); // Asynchronous preparation
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading audio", Toast.LENGTH_SHORT).show();
        }

        // Set up event listener for when media is prepared
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Set SeekBar maximum duration
                seekBar.setMax(mediaPlayer.getDuration());
                // Set TextView duration
                setDurationTextView(mediaPlayer.getDuration());
                // Start playing when prepared
                mediaPlayer.start();
                // Update SeekBar progress
                updateSeekBar();
                isPlaying = true;
                playPauseButton.setImageResource(R.drawable.pause);
            }
        });

        // Set up event listener for errors during preparation
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(AudioPlay.this, "Error during playback", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    private void togglePlayPause() {
        if (isPlaying) {
            pauseAudio();
        } else {
            playAudio();
        }
    }
    private void playAudio() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateSeekBar();
            isPlaying = true;
            playPauseButton.setImageResource(R.drawable.pause);
        }
    }

    private void pauseAudio() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            playPauseButton.setImageResource(R.drawable.play);
        }
    }
    private void updateSeekBar() {
        // Update SeekBar progress every 100 milliseconds
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    // Repeat the update every 100 milliseconds
                    handler.postDelayed(this, 100);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createMediaPlayerWithAttributes() {
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
    }
    private void setDurationTextView(int duration) {
        int minutes = duration / 1000 / 60;
        int seconds = (duration / 1000) % 60;
        String durationText = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        durationTextView.setText(durationText);
    }

    private void createMediaPlayer() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}