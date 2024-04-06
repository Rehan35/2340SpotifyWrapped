package com.example.spotifywrapped2340;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SpotifyLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_spotify);
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();

        TextView wrappedTextView = (TextView) findViewById(R.id.wrapped_text);
        Button connectWithSpotify = (Button) findViewById(R.id.connect_with_spotify_button);
        connectWithSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
