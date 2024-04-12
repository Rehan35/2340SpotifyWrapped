package com.example.spotifywrapped2340.ObjectStructures;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Artist {
    private String artistId;
    private String name;
    private String artistImageUrl;

    private int popularity;

    private ArrayList<String> genres;

    public Artist(String artistId, String name, String artistImageUrl, int popularity, ArrayList<String> genres) {
        this.artistId = artistId;
        this.name = name;
        this.artistImageUrl = artistImageUrl;
        this.popularity = popularity;
        this.genres = genres;
    }

    public String getGenreText() {
        String returnedString = "";

        for (int i = 0; i < genres.size(); i++) {
            returnedString += genres.get(i);
            if (i < genres.size() - 1) {
                returnedString += ", ";
            }
        }
        return returnedString;
    }


    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistImageUrl() {
        return artistImageUrl;
    }

    public void setArtistImageUrl(String artistImageUrl) {
        this.artistImageUrl = artistImageUrl;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }
}
