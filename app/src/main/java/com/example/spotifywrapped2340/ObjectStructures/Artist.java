package com.example.spotifywrapped2340.ObjectStructures;

public class Artist {
    String artistId;
    String name;
    String artistImageUrl;

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtistImageUrl(String artistImageUrl) {
        this.artistImageUrl = artistImageUrl;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistImageUrl() {
        return artistImageUrl;
    }

    public String getName() {
        return name;
    }
}
