package com.example.spotifywrapped2340.UIHelpers;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileGridItem {
    private String text;
    private int imageResource;
    private String imageUrl;
    private Activity activity;

    public ProfileGridItem(String text, int imageResource) {
        this.text = text;
        this.imageResource = imageResource;
    }

    public ProfileGridItem(String text, int imageResource, AppCompatActivity activity) {
        this.text = text;
        this.imageResource = imageResource;
        this.activity = activity;
    }

    public ProfileGridItem(String text, int imageResource, AppCompatActivity activity, String imageUrl) {
        this.text = text;
        this.imageResource = imageResource;
        this.activity = activity;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public Activity getActivity() {
        return this.activity;
    }
}
