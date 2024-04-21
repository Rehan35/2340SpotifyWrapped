package com.example.spotifywrapped2340;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.ObjectStructures.Artist;
import com.example.spotifywrapped2340.ObjectStructures.Track;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;

public class ForYouTracksActivity extends AppCompatActivity {

    RelativeLayout relativelayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.for_you_tracks_activity);

        relativelayout = (RelativeLayout) findViewById(R.id.tracks_card);
        Button backButton = (Button) findViewById(R.id.back_button);

        updateTracksCards();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    private void updateTracksCards() {
        for (int i = 0; i < 5; i++) {

            Track track = SpotifyManager.forYouTracks.get(i);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View trackCardView = inflater.inflate(R.layout.track_card_view, relativelayout, false);

            trackCardView.setId(i + 1);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            TextView rankingNumberText = (TextView) trackCardView.findViewById(R.id.number_text2);
            ImageView trackCardImageView = (ImageView) trackCardView.findViewById(R.id.track_image2);
            TextView trackName = (TextView) trackCardView.findViewById(R.id.track_name2);

            trackName.setId(R.id.track_name2);

            Glide.with(ForYouTracksActivity.this).load(track.getAlbumCoverURL()).into(trackCardImageView);

            TextView albumName = (TextView) trackCardView.findViewById(R.id.album_name2);

            albumName.setId(R.id.album_name2);

            RelativeLayout.LayoutParams albumNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            albumNameParams.setMargins(0, 0, 0, 0);
            albumNameParams.addRule(RelativeLayout.BELOW,R.id.track_name2);
            albumName.setLayoutParams(albumNameParams);

            TextView artistName = (TextView) trackCardView.findViewById(R.id.track_artist2);

            rankingNumberText.setText(String.valueOf(i + 1));
            trackName.setText(track.getTrackName());
            albumName.setText("Album: " + String.valueOf(track.getAlbumName()));
            artistName.setText("Artist: " + track.getArtistName());

            RelativeLayout.LayoutParams artistParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            artistParams.setMargins(0, 0, 0, 0);
            artistParams.addRule(RelativeLayout.BELOW,R.id.album_name2);
            artistName.setLayoutParams(artistParams);


            params.addRule(RelativeLayout.BELOW,i);
            params.setMargins(0, 20, 0, 20);

            trackCardView.setLayoutParams(params);

            relativelayout.addView(trackCardView);
        }
    }
}

