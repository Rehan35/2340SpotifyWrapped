package com.example.spotifywrapped2340;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class DuoWrappedDataActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    private StoriesProgressView storiesProgressView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duo_wrapped);
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(4); // <- set stories
        storiesProgressView.setStoryDuration(1200L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(); // <- start progress
    }

    @Override
    public void onNext() {
        Toast.makeText(this, "onNext", Toast.LENGTH_SHORT).show();
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
