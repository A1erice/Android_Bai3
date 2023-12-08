package com.example.myapplication2;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Objects;

public class MusicPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton playPauseButton;
    private TextView durationTextView;
    private Handler handler;
    private SeekBar seekBar;
    private static final int UPDATE_PROGRESS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Retrieve file path using the correct key
        String path = getIntent().getStringExtra("file");
        System.out.println(path);
        // Initialize UI components
        playPauseButton = findViewById(R.id.playPauseButton);
        seekBar = findViewById(R.id.seekBar);
        // Get metadata
        getMetadata(path);
        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, Uri.parse(path));
        mediaPlayer.setOnCompletionListener(mp -> playPauseButton.setImageResource(R.drawable.ic_play));
        mediaPlayer.start();
        // Get duration
        seekBar.setMax(mediaPlayer.getDuration());
        durationTextView = findViewById(R.id.timeTextView);
        handler = new Handler(msg -> {
            if (msg.what == UPDATE_PROGRESS) {
                updateSeekBar();
                updateDuration();
                return true;
            }
            return false;
        });

        // Set a periodic task to update the progress
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    handler.sendMessage(Message.obtain(handler, UPDATE_PROGRESS, currentPosition));
                }
                handler.postDelayed(this, 1000); // Update every 1000 milliseconds (1 second)
            }
        });
        // Set click listener for play/pause button
        playPauseButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPauseButton.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                playPauseButton.setImageResource(R.drawable.ic_pause);
            }
        });
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

        // Set other listeners and controls as needed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
    private void updateSeekBar() {
        if (mediaPlayer != null) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currentPosition);
        }
    }
    @SuppressLint("DefaultLocale")
    private void updateDuration() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();

        // Calculate current time in minutes and seconds
        int currentSeconds = currentPosition / 1000;
        int currentMinutes = currentSeconds / 60;
        currentSeconds = currentSeconds % 60;

        // Calculate total time in minutes and seconds
        int totalSeconds = duration / 1000;
        int totalMinutes = totalSeconds / 60;
        totalSeconds = totalSeconds % 60;

        // Update the durationTextView with the formatted time
        durationTextView.setText(String.format("%02d:%02d / %02d:%02d", currentMinutes, currentSeconds, totalMinutes, totalSeconds));
    }
    public void getMetadata(String filePath) {
        try {
            // Read the audio file
            AudioFile audioFile = AudioFileIO.read(new File(filePath));

            // Get the tag (metadata) from the audio file
            Tag tag = audioFile.getTag();

            // Retrieve metadata information
            String title = tag.getFirst(FieldKey.TITLE);
            String artist = tag.getFirst(FieldKey.ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);
            String genre = tag.getFirst(FieldKey.GENRE);
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null) {
                // Convert the byte array to a Bitmap
                byte[] imageData = artwork.getBinaryData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                // IMG
                ImageView coverImageView = findViewById(R.id.albumArtImageView);
                coverImageView.setImageBitmap(bitmap);
            }
            // Title
            TextView titleTextView = findViewById(R.id.songTitleTextView);
            titleTextView.setSelected(true);
            if (!Objects.equals(title, "")) titleTextView.setText(title);
            // Artist
            TextView artistTextView = findViewById(R.id.artistTextView);
            artistTextView.setSelected(true);
            if (!Objects.equals(artist, "")) artistTextView.setText(artist);
            // Other info
            TextView infoTextView = findViewById(R.id.infoTextView);
            infoTextView.setSelected(true);
            if (!Objects.equals(album, "") && !Objects.equals(genre, "")) infoTextView.setText(album + " | " + genre);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
