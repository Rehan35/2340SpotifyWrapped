package com.example.spotifywrapped2340;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.example.spotifywrapped2340.UIHelpers.ProfileGridItem;
import com.example.spotifywrapped2340.util.CompletionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.Futures;

import java.io.IOException;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class SavedArtistWrappedActivity extends AppCompatActivity {

    private GridLayout gridLayout;

    @Override
    protected void onStart() {
        super.onStart();
        //SpotifyManager manager = SpotifyManager.getInstance(getApplicationContext());
        //manager.fetchTopArtists(SpotifyManager.TopItemType.artists, "medium_range", 10);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_wrapped);

        gridLayout = findViewById(R.id.gridLayout2);
        ArrayList<ProfileGridItem> gridItemArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(SpotifyManager.getInstance(getApplicationContext()).user.getUserId()).collection("artistpaths").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        try {
                            gridItemArrayList.add(new ProfileGridItem(snapshot.get("wrapped_name").toString(), R.drawable.spotify_wrapped_login_logo, new ArtistWrapped(snapshot.get("json").toString())));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    updateGridLayout(gridItemArrayList);
                }
            }
        });




    }


    private void updateGridLayout(ArrayList<ProfileGridItem> itemsList) {
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