package com.example.spotifywrapped2340.Firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.spotifywrapped2340.MainActivity;
import com.example.spotifywrapped2340.ObjectStructures.SpotifyUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseManager {

    private static FirebaseManager instance;
    private Context context;

    private FirebaseManager(Context context) {
        this.context = context.getApplicationContext(); // Use application context
    }

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void addUserToDatabase(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("Users");

        Map<String, Object> user = new HashMap<>();
        user.put("uID", userId);

        usersCollection.document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Account Created Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Failed to Create Account");
                    }
                });
    }

    public void populateUserSpotifyData(SpotifyUser spotifyUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("Users");

        Map<String, Object> user = new HashMap<>();
        user.put("uID", spotifyUser.getUserId());
        user.put("profileImageUrl", spotifyUser.getProfileImageUrl());
        user.put("followers", spotifyUser.getFollowers());
        user.put("refreshAccessToken", spotifyUser.getRefreshToken());
        user.put("email", spotifyUser.getEmail());
        user.put("spotifyId", spotifyUser.getUserId());

        usersCollection.document(spotifyUser.getUserId()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Spotify Loaded Successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase Populate Error", e.toString());
                        showToast("Failed to Authenticate Spotify");
                    }
                });
    }

}
