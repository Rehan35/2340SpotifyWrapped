package com.example.spotifywrapped2340;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.example.spotifywrapped2340.UIHelpers.ProfileGridItem;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private GridLayout gridLayout;

    @Override
    protected void onStart() {
        super.onStart();
//        SpotifyManager manager = SpotifyManager.getInstance(getApplicationContext());
//        manager.fetchTopArtists(SpotifyManager.TopItemType.artists, "medium_range", 10);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        ImageButton settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        ProfileGridItem[] gridItems = new ProfileGridItem[]{
                new ProfileGridItem("Tracks", R.drawable.tracks_placeholder_card_image, new TracksActivity()),
                new ProfileGridItem("Artists", R.drawable.artists_placeholder_card, new ArtistsActivity()),
                new ProfileGridItem("Playlists", R.drawable.playlists_placeholder_card, new TracksActivity()),
                new ProfileGridItem("Lyrics", R.drawable.lyrics_placeholder_card, new TracksActivity()),
                new ProfileGridItem("For You", R.drawable.foryou_placeholder_card, new TracksActivity()),
                new ProfileGridItem("Browse", R.drawable.browse_placeholder_card, new TracksActivity())
        };

        updateGridLayout(gridItems);
    }

    private void updateGridLayout(ProfileGridItem[] itemsList) {
        // Clear existing views in GridLayout
        gridLayout.removeAllViews();

        // Add new views based on the data list
        for (ProfileGridItem item : itemsList) {
            // Inflate a new instance of your grid item layout
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View itemView = inflater.inflate(R.layout.item_layout, gridLayout, false);

            // Bind data to the layout
            ImageButton imageButton = (ImageButton) itemView.findViewById(R.id.card_image);
            TextView textView = itemView.findViewById(R.id.card_text);

            imageButton.setImageDrawable(ContextCompat.getDrawable(this, item.getImageResource()));
            textView.setText(item.getText());

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), item.getActivity().getClass());
                    startActivity(intent);
                    finish();
                }
            });

            // Add the inflated view to the GridLayout
            gridLayout.addView(itemView);
        }
    }

}
