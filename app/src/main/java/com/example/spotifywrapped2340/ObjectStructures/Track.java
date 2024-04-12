package com.example.spotifywrapped2340.ObjectStructures;

public class Track {
    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumType() {
        return albumType;
    }

    public void setAlbumType(String albumType) {
        this.albumType = albumType;
    }

    public String getAlbumCoverURL() {
        return albumCoverURL;
    }

    public void setAlbumCoverURL(String albumCoverURL) {
        this.albumCoverURL = albumCoverURL;
    }

    String artistId;
    String albumName;

    String artistName;
    String albumType;

    String albumCoverURL;

    String albumId;


}
