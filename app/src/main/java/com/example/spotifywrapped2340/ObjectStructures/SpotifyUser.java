package com.example.spotifywrapped2340.ObjectStructures;

import android.util.Log;

import com.example.spotifywrapped2340.SpotifyDataManagers.JsonReader;
import com.example.spotifywrapped2340.util.CompletionListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SpotifyUser extends User {
    private String spotifyId;
    private String refreshToken;
    private String profileImageUrl;
    private Integer followers;
    private String email;

    public SpotifyUser() {
        super();
        this.setSpotifyId("");
        this.setRefreshToken("");
        this.setProfileImageUrl("");
        this.setFollowers(0);
        this.setEmail("");
    }

    public SpotifyUser(String spotifyId, String refreshToken, String profileImageUrl, Integer followers, String email) {
        this.setSpotifyId(spotifyId);
        this.setRefreshToken(refreshToken);
        this.setProfileImageUrl(profileImageUrl);
        this.setFollowers(followers);
        this.setEmail(email);
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public void populateUserData(String jsonString, String googleId, CompletionListener completionListener) throws IOException {
        try {
            Log.d("User JSON", jsonString);
            JsonReader reader = new JsonReader(jsonString);

            String name = reader.getStringValue("display_name");
            String id = reader.getStringValue("id");
            this.setName(name);
            this.setSpotifyId((String) id);


            Map<String, ArrayList<Object>> returnedMap = reader.getArrayValues("images", new JsonReader.ArrayValue[]{
                    new JsonReader.ArrayValue("url", JsonReader.ReadableType.STRING)
            });

            Map<String, Object> followersMap = reader.getJsonObjectData("followers", new JsonReader.ArrayValue[]{
                    new JsonReader.ArrayValue("total", JsonReader.ReadableType.INTEGER)});

            ArrayList<Object> imageUrls = returnedMap.get("url");
            if (imageUrls != null && !imageUrls.isEmpty()) {
                this.setProfileImageUrl((String) imageUrls.get(0));
            }

            Object followerCount = followersMap.get("total");
            if (followerCount != null) {
                this.setFollowers(Integer.valueOf(String.valueOf(followerCount)));
            }

            String email = reader.getStringValue("email");
            this.setEmail((String) email);

            this.setUserId(googleId);

            Log.d("Spotify User", this.toString());
            completionListener.onComplete("Loaded User Successfully");

        } catch (JSONException e) {

        }
    }

    public String toString() {
        String returnString = "Name: " + getName() + " Email: " + email + " profileImageUrl: " + profileImageUrl + " Followers: " + followers;
        return returnString;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
