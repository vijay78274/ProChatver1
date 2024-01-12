package com.example.prochatver1.Acitivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.prochatver1.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;

import java.util.HashMap;

public class FullVideoActivity extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private ImageView thumbnailImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);
        playerView = findViewById(R.id.playerView);

        // Initialize ExoPlayer
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs( // Set buffer durations in milliseconds
                        5000,   // minBufferMs: Minimum duration of media that must be buffered for playback
                        10000,  // maxBufferMs: Maximum duration of media that must be buffered for playback
                        2500,   // bufferForPlaybackMs: Buffer ahead of the playback position
                        5000    // bufferForPlaybackAfterRebufferMs: Buffer ahead of the playback position after a rebuffer
                )
                .build();
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build();
        playerView.setPlayer(player);

        // Set media item to play (replace "your_video_url_here" with your actual video URL)
        String videoUrl = getIntent().getStringExtra("videoUrl");
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));

        VideoFrameMetadataListener videoFrameMetadataListener = new VideoFrameMetadataListener() {
            @Override
            public void onVideoFrameAboutToBeRendered(long presentationTimeUs, long releaseTimeNs, Format format, @Nullable MediaFormat mediaFormat) {
                if (presentationTimeUs == 3 * 1000 * 1000) { // Capture frame at 3 seconds
                    Bitmap bitmap = getBitmapFromVideo(videoUrl, presentationTimeUs);
                    if (bitmap != null) {
                        // Display the captured frame in the thumbnailImageView
                        thumbnailImageView.setImageBitmap(bitmap);
                    }
                }
            }
        };
        // Prepare the player with the media item
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true); // Start playback when ready
    }

    private Bitmap getBitmapFromVideo(String videoUrl, long timeUs) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoUrl, new HashMap<>());
        return retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the player when the activity is destroyed
        if (player != null) {
            player.release();
        }
    }
}