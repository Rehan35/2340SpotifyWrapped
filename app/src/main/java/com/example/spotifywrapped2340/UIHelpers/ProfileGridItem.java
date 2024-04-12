package com.example.spotifywrapped2340.UIHelpers;

public class ProfileGridItem {
    private String text;
    private int imageResource;

    public ProfileGridItem(String text, int imageResource) {
        this.text = text;
        this.imageResource = imageResource;
    }

    public String getText() {
        return text;
    }

    public int getImageResource() {
        return imageResource;
    }
}
