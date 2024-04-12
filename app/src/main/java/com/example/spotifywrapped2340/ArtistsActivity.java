package com.example.spotifywrapped2340;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.ObjectStructures.Artist;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.example.spotifywrapped2340.UIHelpers.ProfileGridItem;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;

public class ArtistsActivity extends AppCompatActivity {

    RelativeLayout relativelayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artists_activity);

        relativelayout = (RelativeLayout) findViewById(R.id.artist_card);

        ArrayList<String> genres = new ArrayList<>();
        genres.add("rap");
        genres.add("pop");
        genres.add("hip-hop");

        updateArtistCards(new Artist[]{
                new Artist("id1", "J. Cole", "https://www.google.com", 100, genres),
                new Artist("id1", "Post Malone", "https://www.google.com", 70, genres),
                new Artist("id1", "Travis Scott", "https://www.google.com", 50, genres),
                new Artist("id1", "Umi", "https://www.google.com", 60, genres),
                new Artist("id1", "Kanye West", "https://www.google.com", 80, genres)

        });

    }

    private void updateArtistCards(Artist[] artists) {
        for (int i = 0; i < 5; i++) {

            Artist artist = SpotifyManager.topArtists.get(i);
            // Inflate a new instance of your grid item layout
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View artistCardView = inflater.inflate(R.layout.artist_card_view, relativelayout, false);

            artistCardView.setId(i + 1);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            TextView rankingNumberText = (TextView) artistCardView.findViewById(R.id.number_text2);
            ImageView artistImage = (ImageView) artistCardView.findViewById(R.id.artist_image2);
            TextView artistName = (TextView) artistCardView.findViewById(R.id.artist_name2);

            artistName.setId(R.id.artist_name2);

            Glide.with(ArtistsActivity.this).load(artist.getArtistImageUrl()).into(artistImage);

            TextView artistPopularity = (TextView) artistCardView.findViewById(R.id.popularity_score2);

            artistPopularity.setId(R.id.popularity_score2);

            RelativeLayout.LayoutParams popularityParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            popularityParams.setMargins(0, 0, 0, 0);
            popularityParams.addRule(RelativeLayout.BELOW,R.id.artist_name2);
            artistPopularity.setLayoutParams(popularityParams);

            TextView artistGenres = (TextView) artistCardView.findViewById(R.id.artist_genre2);

            rankingNumberText.setText(String.valueOf(i + 1));
            artistName.setText(artist.getName());
            artistPopularity.setText("Popularity: " + String.valueOf(artist.getPopularity()));
            artistGenres.setText(artist.getGenreText());

            RelativeLayout.LayoutParams genreParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            genreParams.setMargins(0, 0, 0, 0);
            genreParams.addRule(RelativeLayout.BELOW,R.id.popularity_score2);
            artistGenres.setLayoutParams(genreParams);


            params.addRule(RelativeLayout.BELOW,i);
            params.setMargins(0, 20, 0, 20);

            artistCardView.setLayoutParams(params);



            // Bind data to the layout

            // Add the inflated view to the GridLayout
            relativelayout.addView(artistCardView);
        }
    }
}
