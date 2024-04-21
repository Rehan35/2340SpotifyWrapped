package com.example.spotifywrapped2340;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;

import jp.shts.android.storiesprogressview.StoriesProgressView;


public class TracksActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    private StoriesProgressView storiesProgressView;
    private TextView topLabel;
    private TextView trackName;
    private TextView artistName;
    private Button backButton;
    private int currentIndex = 0;

    private ImageView imageView;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);
        topLabel = (TextView) findViewById(R.id.topLabel);
        topLabel.setText("Top Tracks!");

        trackName = (TextView) findViewById(R.id.trackLabel);
        artistName = (TextView) findViewById(R.id.artistLabel);
        imageView = (ImageView) findViewById(R.id.mainImage);
        backButton = (Button) findViewById(R.id.wrapped_return_button);

        artistName.setText("#" + (currentIndex + 1));
        trackName.setText(SpotifyManager.topTracks.get(currentIndex).getTrackName());
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
