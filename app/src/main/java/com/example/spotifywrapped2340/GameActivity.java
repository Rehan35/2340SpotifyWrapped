package com.example.spotifywrapped2340;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.ObjectStructures.Artist;
import com.example.spotifywrapped2340.ObjectStructures.Track;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.example.spotifywrapped2340.UIHelpers.ProfileGridItem;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.protocol.types.Album;

import java.util.Random;
import java.util.concurrent.Executor;

public class GameActivity extends AppCompatActivity {

    private boolean isAlbum = true;
    private Artist artist;
    private Track album;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        ImageView coverImageView = (ImageView) findViewById(R.id.game_image);
        Button newArtist = (Button) findViewById(R.id.new_artist_image);
        Button newAlbum = (Button) findViewById(R.id.new_album_image);
        Button guessButton = (Button) findViewById(R.id.guess_button);
        Button backButton = (Button) findViewById(R.id.back_button);

        Random rand = new Random();

        int randomIndex = rand.nextInt(isAlbum ? SpotifyManager.getInstance(getApplicationContext()).getTopArtists().size() : SpotifyManager.getInstance(getApplicationContext()).getTopTracks().size() + 1);

        if (isAlbum) {
            this.album = SpotifyManager.getInstance(getApplicationContext()).getTopTracks().get(randomIndex);
        } else {
            this.artist = SpotifyManager.getInstance(getApplicationContext()).getTopArtists().get(randomIndex);
        }

        String imageUrl = isAlbum ? SpotifyManager.topTracks.get(randomIndex).getAlbumCoverURL() : SpotifyManager.topArtists.get(randomIndex).getArtistImageUrl();

        Glide.with(GameActivity.this).load(imageUrl).into(coverImageView);

        newArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlbum = false;
                int randomIndex = rand.nextInt(SpotifyManager.getInstance(getApplicationContext()).getTopArtists().size() + 1);
                artist = SpotifyManager.getInstance(getApplicationContext()).getTopArtists().get(randomIndex);

                String imageUrl = SpotifyManager.topArtists.get(randomIndex).getArtistImageUrl();

                Glide.with(GameActivity.this).load(imageUrl).into(coverImageView);
            }
        });

        newAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlbum = true;
                int randomIndex = rand.nextInt(SpotifyManager.getInstance(getApplicationContext()).getTopTracks().size() + 1);
                album = SpotifyManager.getInstance(getApplicationContext()).getTopTracks().get(randomIndex);

                String imageUrl = SpotifyManager.topTracks.get(randomIndex).getAlbumCoverURL();

                Glide.with(GameActivity.this).load(imageUrl).into(coverImageView);
            }
        });

        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText guessText = (EditText) findViewById(R.id.guess_text_field);
                String guessedInput = guessText.getText().toString();

                Log.d("ANSWER", isAlbum ? album.getTrackName() : artist.getName());

                String correctName = isAlbum ? album.getTrackName() : artist.getName();

                if (guessedInput.toLowerCase().equals(correctName.toLowerCase())) {
                    Toast.makeText(GameActivity.this, "Way to go", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameActivity.this, "Sorry, that is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
