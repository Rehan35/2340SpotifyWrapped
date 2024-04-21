package com.example.spotifywrapped2340;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.example.spotifywrapped2340.ObjectStructures.Artist;
import com.example.spotifywrapped2340.util.CompletionListener;

import java.io.IOException;
import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class ForYouActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private StoriesProgressView storiesProgressView;
    private TextView topLabel;
    private TextView trackName;
    private TextView artistName;
    private int currentIndex = 0;

    private ImageView imageView;
    private ArrayList<Artist> relatedArtists = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.wrapped);
        layout.setBackgroundResource(R.drawable.artists_activity_gradient);

        topLabel = (TextView) findViewById(R.id.topLabel);
        trackName = (TextView) findViewById(R.id.trackLabel);
        artistName = (TextView) findViewById(R.id.artistLabel);
        imageView = (ImageView) findViewById(R.id.mainImage);

        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        fetchRelatedArtistsAndUpdateUI();

        storiesProgressView.setStoriesListener(this);
    }

    private void fetchRelatedArtistsAndUpdateUI() {
        String artistId = SpotifyManager.topArtists.get(currentIndex).getArtistId();
        SpotifyManager.getInstance(getApplicationContext()).fetchRelatedArtists(artistId, new CompletionListener() {
            @Override
            public void onComplete(String result) {
                runOnUiThread(() -> {
                    relatedArtists.clear();
                    relatedArtists.addAll(SpotifyManager.topArtists);
                    updateUI();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(ForYouActivity.this, "Failed to fetch related artists", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUI() {
        if (relatedArtists.isEmpty()) {
            Toast.makeText(this, "No related artists found", Toast.LENGTH_SHORT).show();
            return;
        }

        artistName.setText("#" + (currentIndex + 1));
        trackName.setText(relatedArtists.get(currentIndex).getName());
        Glide.with(this).load(relatedArtists.get(currentIndex).getArtistImageUrl()).into(imageView);

        storiesProgressView.setStoriesCount(relatedArtists.size());
        storiesProgressView.setStoryDuration(2400L);
        storiesProgressView.startStories(currentIndex);
    }

    @Override
    public void onNext() {
        if (currentIndex < relatedArtists.size() - 1) {
            currentIndex++;
            updateUI();
        }
    }

    @Override
    public void onPrev() {
        if (currentIndex > 0) {
            currentIndex--;
            updateUI();
        }
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "End of related artists", Toast.LENGTH_SHORT).show();
    }
}
