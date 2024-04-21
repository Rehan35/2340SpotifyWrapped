package com.example.spotifywrapped2340;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;

import jp.shts.android.storiesprogressview.StoriesProgressView;    import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;


public class TracksActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    private StoriesProgressView storiesProgressView;
    private TextView topLabel;
    private TextView trackName;
    private TextView artistName;
    private int currentIndex = 0;

    private ImageView imageView;
    private SpotifyAppRemote obj;





    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder("3f2eac4dbbb0498194d8b5d955949c1a")
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(@NonNull SpotifyAppRemote spotifyAppRemote) {
                obj = spotifyAppRemote;
                Log.d("MainActivity", "Connected to Spotify!");

                // Now you can use mSpotifyAppRemote to control playback, etc.
                playTrack("spotify:track:4uLU6hMCjMI75M1A2tKUQC");
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("MainActivity", "Failed to connect to Spotify", error);
                // Handle connection failure here
            }
        });



        topLabel = (TextView) findViewById(R.id.topLabel);
        topLabel.setText("Top Tracks!");

        trackName = (TextView) findViewById(R.id.trackLabel);
        artistName = (TextView) findViewById(R.id.artistLabel);
        imageView = (ImageView) findViewById(R.id.mainImage);

        artistName.setText("#" + (currentIndex + 1));
        trackName.setText(SpotifyManager.topTracks.get(currentIndex).getTrackName());
        Glide.with(TracksActivity.this).load(SpotifyManager.topTracks.get(currentIndex).getAlbumCoverURL()).into(imageView);




//        if (SpotifyManager.getInstance(getApplicationContext()).topArtists.size() == 0) {

//        }
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(10); // <- set stories
        storiesProgressView.setStoryDuration(2400L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(); // <- start progress
    }

    private void playTrack (String trackUri) {
        if (obj != null && obj.isConnected()) {
            obj.getPlayerApi().play(trackUri);
        } else {
            Log.e("MainActivity", "Cannot play track: Spotify connection not established or disconnected.");
            // Handle the case where Spotify connection is not established or disconnected
        }
    }
    @Override
    public void onNext() {
        currentIndex++;
        artistName.setText("#" + (currentIndex + 1));
        trackName.setText(SpotifyManager.topTracks.get(currentIndex).getTrackName());
        Glide.with(TracksActivity.this).load(SpotifyManager.topTracks.get(currentIndex).getAlbumCoverURL()).into(imageView);

    }

    @Override
    public void onPrev() {
        // Call when finished revserse animation.
        Toast.makeText(this, "onPrev", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "onComplete", Toast.LENGTH_SHORT).show();
    }
}
