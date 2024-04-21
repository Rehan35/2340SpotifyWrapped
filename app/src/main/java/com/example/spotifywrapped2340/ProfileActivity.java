package com.example.spotifywrapped2340;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.ObjectStructures.Track;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.example.spotifywrapped2340.UIHelpers.ProfileGridItem;
import com.example.spotifywrapped2340.util.CompletionListener;
import com.google.common.util.concurrent.Futures;

import java.io.IOException;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ProfileActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    String apiKey = "AIzaSyAFav3XHHlyWER68mm9GuQ-C7WL5z7aQWI";

    @Override
    protected void onStart() {
        super.onStart();
        //SpotifyManager manager = SpotifyManager.getInstance(getApplicationContext());
        //manager.fetchTopArtists(SpotifyManager.TopItemType.artists, "medium_range", 10);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        if (SpotifyManager.getInstance(getApplicationContext()).getAccessToken() == null) {
            getToken();
        }

        ImageButton settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        gridLayout = findViewById(R.id.gridLayout);
        ProfileGridItem[] gridItems = new ProfileGridItem[]{
                new ProfileGridItem("Tracks", R.drawable.tracks_placeholder_card_image, new TracksActivity()),
                new ProfileGridItem("Artists", R.drawable.artists_placeholder_card, new ArtistWrapped()),
                new ProfileGridItem("Play a Game", R.drawable.playlists_placeholder_card, new GameActivity()),
                new ProfileGridItem("Lyrics", R.drawable.lyrics_placeholder_card, new TracksActivity()),
                new ProfileGridItem("For You", R.drawable.foryou_placeholder_card, new TracksActivity()),
                new ProfileGridItem("Browse", R.drawable.browse_placeholder_card, new TracksActivity())
        };

        updateGridLayout(gridItems);

        Log.d("LOGGGG", String.valueOf(SpotifyManager.topTracksLong));
        List<String> tracks = new ArrayList<>();
        for (Track track : SpotifyManager.topTracks) {
            tracks.add(track.getTrackName());
            Log.d("TRACKSS", String.valueOf(tracks));
        }

        String result = "";
        int count = Math.min(tracks.size(), 4);
        for (int i = 0; i < count; i++) {
            result += tracks.get(i);
            if (i < count - 1) {
                result += ", ";
            }
        }

        GenerativeModel gm = new GenerativeModel("gemini-pro", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText("Write just 4 words to describe how someone acts, thinks, and dresses based on their favorite songs: " + result)
                .build();

        Executor executor = ContextCompat.getMainExecutor(this);
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.d("API Call", "Success: " + resultText);
                runOnUiThread(() -> {
                    TextView apiResultTextView = findViewById(R.id.api_result_text_view);
                    apiResultTextView.setText(resultText);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API Call", "Error calling API", t);
            }
        }, executor);
    }


    private void updateGridLayout(ProfileGridItem[] itemsList) {
        gridLayout.removeAllViews();

        for (ProfileGridItem item : itemsList) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View itemView = inflater.inflate(R.layout.item_layout, gridLayout, false);

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
            gridLayout.addView(itemView);
        }
    }

    public void getToken() {
        final AuthorizationRequest request = SpotifyManager.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        try {
            AuthorizationClient.openLoginActivity(ProfileActivity.this, 0, request);
        } catch (Exception e) {
            Log.d("TOKEN ERROR", e.toString());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (response == null) {
            Log.d("TOKEN FAILURE", "response is null");
        } else if (response.getAccessToken() == null) {
            Log.d("TOKEN FAILURE", "access token is null");
        } else {
//            Intent intent = new Intent(SpotifyLoginActivity.this, SpotifyLoginActivity.class);
//            startActivity(intent);
            Log.d("TOKEN SUCCESS", response.getAccessToken());
            SpotifyManager.setAccessToken(response.getAccessToken());
            SpotifyManager.getInstance(getApplicationContext()).getUserProfile(this);
        }
    }
    public void updateProfileViews(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String displayName = jsonObject.getString("display_name");
            int followersCount = jsonObject.getJSONObject("followers").getInt("total");
            String profileImageUrl = null;

            JSONArray imagesArray = jsonObject.getJSONArray("images");
            if (imagesArray.length() > 0) {
                profileImageUrl = imagesArray.getJSONObject(0).getString("url");
            }

            TextView displayNameTextView = findViewById(R.id.display_name_text);
            TextView followersTextView = findViewById(R.id.followers_text);
            ImageView profileImageView = findViewById(R.id.profile_image_view);

            displayNameTextView.setText(displayName);
            followersTextView.setText(followersCount + " Followers");

            if (profileImageUrl != null) {
                Glide.with(this).load(profileImageUrl).into(profileImageView);
            } else {
                Glide.with(this).load(R.drawable.default_profile).into(profileImageView);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Failed to parse user data", Toast.LENGTH_LONG).show();
        }
    }



}