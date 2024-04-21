package com.example.spotifywrapped2340;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;

import jp.shts.android.storiesprogressview.StoriesProgressView;    import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.spotify.android.appremote.api.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;import com.spotify.android.appremote.api.ConnectionParams;


import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


public class TracksActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    private StoriesProgressView storiesProgressView;
    private TextView topLabel;
    private TextView trackName;
    private TextView artistName;
    private Button backButton;
    private int currentIndex = 0;

    private ImageView imageView;
    private SpotifyAppRemote obj;

    private Button saveButton;

    public TracksActivity(String json) throws JSONException {
        SpotifyManager.getInstance(TracksActivity.this).fetchTopTracks(json);
    }

    public TracksActivity(){}




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
                        .setRedirectUri("spotify-wrapped-2340://auth")
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(@NonNull SpotifyAppRemote spotifyAppRemote) {
                obj = spotifyAppRemote;
                Log.d("MainActivity", "Connected to Spotify!");

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
        backButton = (Button) findViewById(R.id.wrapped_return_button);

        artistName.setText("#" + (currentIndex + 1));
        trackName.setText(SpotifyManager.topTracks.get(currentIndex).getTrackName());
        saveButton = (Button) findViewById(R.id.save_button);

        String[] choices = {"Item One", "Item Two", "Item Three"};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(TracksActivity.this);
        final EditText edittext = new EditText(TracksActivity.this);
        builder.setView(edittext);
        builder.setTitle("Name your Wrapped");
        HashMap<String, Object> nestedData = new HashMap<>();
        builder
                .setPositiveButton("Save", (dialog, which) -> {
                    nestedData.put("json", SpotifyManager.getInstance(getApplicationContext()).trackString);
                    nestedData.put("image_url", SpotifyManager.topTracks.get(0).getAlbumCoverURL());
                    nestedData.put("wrapped_name", edittext.getText().toString());
                    db.collection("Users").document(SpotifyManager.getInstance(getApplicationContext()).user.getUserId()).collection("trackpaths").document().set(nestedData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("SUCCESS", "RAHHHH");
                        }
                    });
                    Log.d("Dialog", SpotifyManager.getInstance(getApplicationContext()).user.getUserId());

                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });

        AlertDialog dialog = builder.create();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        Glide.with(TracksActivity.this).load(SpotifyManager.topTracks.get(currentIndex).getAlbumCoverURL()).into(imageView);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TracksActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });



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
