package com.example.spotifywrapped2340;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.spotifywrapped2340.ObjectStructures.Artist;
import com.example.spotifywrapped2340.ObjectStructures.Track;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import org.json.JSONException;

import java.util.ArrayList;

import jp.shts.android.storiesprogressview.StoriesProgressView;


public class ArtistWrapped extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    private StoriesProgressView storiesProgressView;
    private TextView topLabel;
    private TextView trackName;
    private TextView artistName;

    private Spinner timeRangeSpinner;

    private int currentIndex = 0;

    private ImageView imageView;


    private ArrayList<Artist> topArtists = SpotifyManager.topArtistsShort; //change if necessary

    public ArtistWrapped(String json) throws JSONException {
        SpotifyManager.getInstance(ArtistWrapped.this).fetchTopArtists(json);
    }

    public ArtistWrapped(){}

    private Button saveButton;



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.wrapped);
        layout.setBackgroundResource(R.drawable.artists_activity_gradient);
        topLabel = (TextView) findViewById(R.id.topLabel);
        topLabel.setText("Top Artists!");

        trackName = (TextView) findViewById(R.id.trackLabel);
        artistName = (TextView) findViewById(R.id.artistLabel);
        imageView = (ImageView) findViewById(R.id.mainImage);
        Button backButton = (Button) findViewById(R.id.wrapped_return_button);

        timeRangeSpinner = findViewById(R.id.timeRangeSpinner);
        String[] timeRanges = {"Short Term", "Medium Term", "Long Term"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeRanges);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeRangeSpinner.setAdapter(adapter);


        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int selectedItemPosition = preferences.getInt("selectedItemPosition", 0);
        timeRangeSpinner.setSelection(selectedItemPosition);

        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle selection
                String selectedTimeRange = timeRanges[position];
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("selectedItemPosition", position);
                editor.apply();
                if (selectedTimeRange.equals("Short Term")) {
                    topArtists = SpotifyManager.topArtistsShort;
                    trackName.setText(topArtists.get(currentIndex).getName());
                    Glide.with(ArtistWrapped.this).load(topArtists.get(currentIndex).getArtistImageUrl()).into(imageView);
                } else if (selectedTimeRange.equals("Medium Term")) {
                    topArtists = SpotifyManager.topArtistsMedium;
                    trackName.setText(topArtists.get(currentIndex).getName());
                    Glide.with(ArtistWrapped.this).load(topArtists.get(currentIndex).getArtistImageUrl()).into(imageView);
                } else if (selectedTimeRange.equals("Long Term")) {
                    topArtists = SpotifyManager.topArtistsLong;
                    trackName.setText(topArtists.get(currentIndex).getName());
                    Glide.with(ArtistWrapped.this).load(topArtists.get(currentIndex).getArtistImageUrl()).into(imageView);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                String selectedTimeRange = timeRanges[0];
            }
        });

        artistName.setText("#" + (currentIndex + 1));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtistWrapped.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        saveButton = (Button) findViewById(R.id.save_button);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(ArtistWrapped.this);
        final EditText edittext = new EditText(ArtistWrapped.this);
        builder.setView(edittext);
        builder.setTitle("Name your Wrapped");
        HashMap<String, Object> nestedData = new HashMap<>();
        builder
                .setPositiveButton("Save", (dialog, which) -> {
                    nestedData.put("json", SpotifyManager.getInstance(getApplicationContext()).artistString);
                    nestedData.put("image_url", SpotifyManager.topTracks.get(0).getAlbumCoverURL());
                    nestedData.put("wrapped_name", edittext.getText().toString());
                    db.collection("Users").document(SpotifyManager.getInstance(getApplicationContext()).user.getUserId()).collection("artistpaths").document().set(nestedData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("SUCCESS", "RAHHHH");
                        }
                    });
                    Log.d("Dialog", SpotifyManager.getInstance(getApplicationContext()).user.getUserId());

                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });

        AlertDialog dialog = builder.create();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
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
        trackName.setText(topArtists.get(currentIndex).getName());
        Glide.with(ArtistWrapped.this).load(topArtists.get(currentIndex).getArtistImageUrl()).into(imageView);

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
