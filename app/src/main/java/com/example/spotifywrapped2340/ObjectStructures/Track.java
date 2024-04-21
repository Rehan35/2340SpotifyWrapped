package com.example.spotifywrapped2340.ObjectStructures;

public class Track {

    public Track(String artistId, String trackName, String artistName, String albumType, String albumCoverURL) {
        this.artistId = artistId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumType = albumType;
        this.albumCoverURL = albumCoverURL;
        this.albumName = "Album";
        this.trackId = "1";
    }

    public Track(String artistId, String trackName, String artistName, String albumType, String albumCoverURL, String albumName, String trackId) {
        this.artistId = artistId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumType = albumType;
        this.albumCoverURL = albumCoverURL;
        this.albumName = albumName;
        this.trackId = trackId;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
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

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackId() {
        return this.trackId;
    }

    String artistId;
    String trackName;


    String artistName;
    String albumType;

    String albumCoverURL;

    String albumName;

    String trackId;


}
