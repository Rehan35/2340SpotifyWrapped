package com.example.spotifywrapped2340.SpotifyDataManagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.spotifywrapped2340.ObjectStructures.Artist;
import com.example.spotifywrapped2340.ObjectStructures.SpotifyUser;
import com.example.spotifywrapped2340.ObjectStructures.Track;
import com.example.spotifywrapped2340.ProfileActivity;
import com.example.spotifywrapped2340.util.CompletionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyManager {

    public static final String CLIENT_ID = "3f2eac4dbbb0498194d8b5d955949c1a";
    public static final String REDIRECT_URI = "spotify-wrapped-2340://auth";
    private static String mAccessToken;

    private static String mAccessCode;

    public static SpotifyUser user;

    private final static OkHttpClient mOkHttpClient = new OkHttpClient();
    private static Call mCall;
    private static SpotifyManager instance;
    private static Context context;

    public static ArrayList<Track> forYouTracks = new ArrayList<>();
    public static ArrayList<Artist> topArtists = new ArrayList<>();
    public static ArrayList<Artist> topArtistsShort = new ArrayList<>();
    public static ArrayList<Artist> topArtistsMedium = new ArrayList<>();
    public static ArrayList<Artist> topArtistsLong = new ArrayList<>();

    public static ArrayList<Track> topTracks = new ArrayList<>();

    public static ArrayList<Track> topTracksShort = new ArrayList<>();
    public static ArrayList<Track> topTracksMedium = new ArrayList<>();
    public static ArrayList<Track> topTracksLong = new ArrayList<>();


    public String artistString = "";

    public String trackString = "";

    private static Map<String, Integer> genreMap;

    private SpotifyManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized SpotifyManager getInstance(Context context) {
        if (instance == null) {
            instance = new SpotifyManager(context);
        }
        return instance;
    }

    public static void setAccessToken(String newToken) {
        SpotifyManager.mAccessToken = newToken;
    }

    public static AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "streaming", "playlist-read-private", "playlist-read-collaborative", "user-follow-read", "user-top-read", "user-library-read", })
                .setCampaign("your-campaign-token")
                .build();
    }

    public static enum TopItemType {
        artists,
        tracks
    }

    public ArrayList<Artist> fetchTopTracks(String json) throws JSONException {
        String responseString = json;

        trackString = responseString;

        Log.d("Spotify Data", responseString);

        JsonReader reader = new JsonReader(responseString);

        JSONArray albums = reader.jsonObject.getJSONArray("items");

        for (int i = 0; i < albums.length(); i++) {
            JSONObject album = albums.getJSONObject(i).getJSONObject("album");
            String albumType = album.getString("album_type");
            int totalTracks = album.getInt("total_tracks");
            String name = album.getString("name");

            JSONArray artists = album.getJSONArray("artists");
            String artistId = artists.getJSONObject(0).getString("id");
            String artistName = artists.getJSONObject(0).getString("name");

            JSONArray images = album.getJSONArray("images");
            String albumCoverImageURL = images.getJSONObject(0).getString("url");
            Log.d("Album Data", albumType + " " + totalTracks + " " + name + " " + artistId + " " + albumCoverImageURL);
            topTracks.add(new Track(artistId, name, artistName, albumType, albumCoverImageURL));
        }
        return new ArrayList<>();

    }

    public ArrayList<Artist> fetchTopTracks(TopItemType type, String time_range, int limit, CompletionListener completionListener) {
        ArrayList<Artist> artistsList = new ArrayList<Artist>();

        final Request request;

        if (time_range.equals("short_term")) {
            request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/" + type.toString() + "?time_range=short_term")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();
        } else if (time_range.equals("medium_term")) {
            request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/" + type.toString() + "?time_range=medium_term")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();
        } else {
            request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/" + type.toString() + "?time_range=long_term")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();
        }

//        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(context, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    String responseString = response.body().string();

                    trackString = responseString;

                    Log.d("Spotify Data", responseString);

                    JsonReader reader = new JsonReader(responseString);

                    JSONArray albums = reader.jsonObject.getJSONArray("items");

                    for (int i = 0; i < albums.length(); i++) {
                        JSONObject album = albums.getJSONObject(i).getJSONObject("album");
                        String albumType = album.getString("album_type");
                        String albumName = album.getString("name");
                        int totalTracks = album.getInt("total_tracks");

                        JSONObject track = albums.getJSONObject(i);

                        String trackName = track.getString("name");
                        String trackId = track.getString("id");


                        JSONArray artists = album.getJSONArray("artists");
                        String artistId = artists.getJSONObject(0).getString("id");
                        String artistName = artists.getJSONObject(0).getString("name");

                        JSONArray images = album.getJSONArray("images");
                        String albumCoverImageURL = images.getJSONObject(0).getString("url");
                        Log.d("Album Data", albumType + " " + totalTracks + " " + trackName + " " + artistId + " " + albumCoverImageURL + " " + albumName);

                        Track newTrack = new Track(artistId, trackName, artistName, albumType, albumCoverImageURL, albumName, trackId);

                        if (time_range.equals("short_term")) {
                            topTracksShort.add(newTrack);
                            topTracks = topTracksShort;
                        } else if (time_range.equals("medium_term")) {
                            topTracksMedium.add(newTrack);
                            topTracks = topTracksMedium;
                        } else if (time_range.equals("long_term")) {
                            topTracksLong.add(newTrack);
                            topTracks = topTracksLong;
                        }
                        //topTracks.add(new Track(artistId, name, artistName, albumType, albumCoverImageURL));
                    }

                    completionListener.onComplete("Fetched tracks successfully");

                } catch (Exception e) {
                    Log.d("JSON TRACKS", "Failed to parse data: " + e);
                }
            }
        });
        return new ArrayList<>();
    }


    public void fetchTopArtists(String json) throws JSONException {

        String responseString = json;

        JsonReader reader = new JsonReader(responseString);

        Map<String, ArrayList<Object>> map = reader.getArrayValues("items", new JsonReader.ArrayValue[]{
                new JsonReader.ArrayValue("name", JsonReader.ReadableType.STRING),
                new JsonReader.ArrayValue("id", JsonReader.ReadableType.STRING)
        });

        ArrayList<Object> names = map.get("name");
        ArrayList<Object> ids = map.get("id");

        if (names != null && ids != null) {
            for (int i = 0; i < names.size(); i++) {
                Artist artist = new Artist("", "", "", 0, new ArrayList<>());
                artist.setName((String)names.get(i));
                artist.setArtistId((String) ids.get(i));
                topArtists.add(artist);
                Log.d("Name, Id", (String) names.get(i) + ", " + (String) ids.get(i));
            }
        }

        JSONArray itemsJsonArray = reader.jsonObject.getJSONArray("items");
        genreMap = new HashMap<>();

        for (int i = 0; i < itemsJsonArray.length(); i++) {
            JSONObject itemData = itemsJsonArray.getJSONObject(i);
            JSONArray imagesArray = itemData.getJSONArray("images");
            JSONArray genres = (JSONArray) itemData.get("genres");

            ArrayList<String> artistGenres = new ArrayList<>();

            for (int j = 0; j < genres.length(); j++) {
                String genre = (String) genres.get(j);
                artistGenres.add(genre);
                genreMap.put(genre, genreMap.getOrDefault(genre, 0) + 1);
            }

            String url = imagesArray.getJSONObject(0).getString("url");
            topArtists.get(i).setArtistImageUrl(url);
            Log.d("URL", url);
        }
    }
    public void fetchTopArtists(TopItemType type, String time_range, int limit, CompletionListener listener) {
        Log.d("HELLO", "WORLD");
        final Request request;

        if (time_range.equals("short_term")) {
            request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/" + type.toString() + "?time_range=short_term")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();
        } else if (time_range.equals("medium_term")) {
            request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/" + type.toString() + "?time_range=medium_term")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();
        } else {
            request = new Request.Builder()
                    .url("https://api.spotify.com/v1/me/top/" + type.toString() + "?time_range=long_term")
                    .addHeader("Authorization", "Bearer " + mAccessToken)
                    .build();
        }
//        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
//                Toast.makeText(context, "Failed to fetch data, watch Logcat for more details",
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    String responseString = response.body().string();

                    artistString = responseString;

                    Log.d("Spotify Data", responseString);

                    JsonReader reader = new JsonReader(responseString);

                    Map<String, ArrayList<Object>> map = reader.getArrayValues("items", new JsonReader.ArrayValue[]{
                            new JsonReader.ArrayValue("name", JsonReader.ReadableType.STRING),
                            new JsonReader.ArrayValue("id", JsonReader.ReadableType.STRING)
                    });

                    ArrayList<Object> names = map.get("name");
                    ArrayList<Object> ids = map.get("id");

                    if (names != null && ids != null) {
                        for (int i = 0; i < names.size(); i++) {
                            Artist artist = new Artist("", "", "", 0, new ArrayList<>());
                            artist.setName((String)names.get(i));
                            artist.setArtistId((String) ids.get(i));
                            if (time_range.equals("short_term")) {
                                topArtistsShort.add(artist);
                                topArtists = topArtistsShort;
                            } else if (time_range.equals("medium_term")) {
                                topArtistsMedium.add(artist);
                                topArtists = topArtistsMedium;
                            } else if (time_range.equals("long_term")) {
                                topArtistsLong.add(artist);
                                topArtists = topArtistsLong;
                            }
                            //topArtists.add(artist);
                            Log.d("Name, Id", (String) names.get(i) + ", " + (String) ids.get(i));
                        }
                    }

                    JSONArray itemsJsonArray = reader.jsonObject.getJSONArray("items");
                    genreMap = new HashMap<>();

                    for (int i = 0; i < itemsJsonArray.length(); i++) {
                        JSONObject itemData = itemsJsonArray.getJSONObject(i);
                        JSONArray imagesArray = itemData.getJSONArray("images");

                        if (imagesArray.length() > 0) {
                            String url = imagesArray.getJSONObject(0).getString("url");
                            if (time_range.equals("short_term")) {
                                topArtistsShort.get(i).setArtistImageUrl(url);
                                topArtists = topArtistsShort;
                            } else if (time_range.equals("medium_term")) {
                                topArtistsMedium.get(i).setArtistImageUrl(url);
                                topArtists = topArtistsMedium;
                            } else if (time_range.equals("long_term")) {
                                topArtistsLong.get(i).setArtistImageUrl(url);
                                topArtists = topArtistsLong;
                            }
                            Log.d("URL", url);
                        } else {
                            Log.d("URL", "No image available for artist at index " + i);
                        }

                        JSONArray genres = (JSONArray) itemData.get("genres");

                        ArrayList<String> artistGenres = new ArrayList<>();

                        for (int j = 0; j < genres.length(); j++) {
                            String genre = (String) genres.get(j);
                            artistGenres.add(genre);
                            genreMap.put(genre, genreMap.getOrDefault(genre, 0) + 1);
                        }
                    }
                    listener.onComplete("Task completed successfully!");
                } catch (Exception e) {
                    Log.d("JSON ARTISTS", "Failed to parse data: " + e);
                }
            }
        });
    }

    public void fetchRelatedArtists(String artistId, CompletionListener completionListener) {
        final String url = "https://api.spotify.com/v1/artists/" + artistId + "/related-artists";
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch related artists: " + e);
                Toast.makeText(context, "Failed to fetch related artists, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
                completionListener.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        JSONArray artists = jsonResponse.getJSONArray("artists");
                        ArrayList<Artist> relatedArtists = new ArrayList<>();

                        for (int i = 0; i < artists.length(); i++) {
                            JSONObject artist = artists.getJSONObject(i);
                            String id = artist.getString("id");
                            String name = artist.getString("name");
                            JSONArray images = artist.getJSONArray("images");
                            String imageUrl = images.getJSONObject(0).getString("url");

                            relatedArtists.add(new Artist(id, name, imageUrl, 0, new ArrayList<>()));
                        }

                        topArtists.addAll(relatedArtists);
                        completionListener.onComplete("Fetched related artists successfully!");
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (Exception e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    completionListener.onError(e);
                }
            }
        });
    }


    public ArrayList<Track> getTopTracks() {
        return topTracks;
    }

    public ArrayList<Artist> getTopArtists() {
        return topArtists;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public ArrayList<Artist> forYouArtists(CompletionListener completionListener) {
        String seedArtists = "";
        String seedTracks = "";
        String seedGenres = "";

        String[] potentialGenres = new String[]{"acoustic", "afrobeat", "alt-rock", "alternative", "ambient", "anime", "black-metal", "bluegrass", "blues", "bossanova", "brazil", "breakbeat", "british", "cantopop", "chicago-house", "children", "chill", "classical", "club", "comedy", "country", "dance", "dancehall", "death-metal", "deep-house", "detroit-techno", "disco", "disney", "drum-and-bass", "dub", "dubstep", "edm", "electro", "electronic", "emo", "folk", "forro", "french", "funk", "garage", "german", "gospel", "goth", "grindcore", "groove", "grunge", "guitar", "happy", "hard-rock", "hardcore", "hardstyle", "heavy-metal", "hip-hop", "holidays", "honky-tonk", "house", "idm", "indian", "indie", "indie-pop", "industrial", "iranian", "j-dance", "j-idol", "j-pop", "j-rock", "jazz", "k-pop", "kids", "latin", "latino", "malay", "mandopop", "metal", "metal-misc", "metalcore", "minimal-techno", "movies", "mpb", "new-age", "new-release", "opera", "pagode", "party", "philippines-opm", "piano", "pop", "pop-film", "post-dubstep", "power-pop", "progressive-house", "psych-rock", "punk", "punk-rock", "r-n-b", "rainy-day", "reggae", "reggaeton", "road-trip", "rock", "rock-n-roll", "rockabilly", "romance", "sad", "salsa", "samba", "sertanejo", "show-tunes", "singer-songwriter", "ska", "sleep", "songwriter", "soul", "soundtracks", "spanish", "study", "summer", "swedish", "synth-pop", "tango", "techno", "trance", "trip-hop", "turkish", "work-out", "world-music"};

        for (int i = 0; i < 2; i++) {
            seedArtists += (i != 0 ? "%2C" : "") + topArtists.get(i).getArtistId();
            seedTracks += (i != 0 ? "%2C" : "") + topTracks.get(i).getTrackId();
        }

        Random random = new Random();
        for (int i = 0; i < 1; i++) {
            seedGenres += "%2C" + potentialGenres[random.nextInt(potentialGenres.length)];
        }
        seedGenres = seedGenres.substring(3);

        String requestUrl = "https://api.spotify.com/v1/recommendations?" + "seed_artists=" + seedArtists + "&seed_genres=" + seedGenres + "&seed_tracks=" + seedTracks;
        Log.d("REQUEST URL", requestUrl);

        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
//                Toast.makeText(context, "Failed to fetch data, watch Logcat for more details",
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    String responseString = response.body().string();


                    Log.d("RESPONSE FOR YOU", responseString);

                    JsonReader reader = new JsonReader(responseString);

                    JSONArray albums = reader.jsonObject.getJSONArray("tracks");

                    for (int i = 0; i < albums.length(); i++) {
                        JSONObject album = albums.getJSONObject(i).getJSONObject("album");
                        String albumType = album.getString("album_type");
                        String albumName = album.getString("name");
                        int totalTracks = album.getInt("total_tracks");

                        JSONObject track = albums.getJSONObject(i);

                        String trackName = track.getString("name");
                        String trackId = track.getString("id");


                        JSONArray artists = album.getJSONArray("artists");
                        String artistId = artists.getJSONObject(0).getString("id");
                        String artistName = artists.getJSONObject(0).getString("name");

                        JSONArray images = album.getJSONArray("images");
                        String albumCoverImageURL = images.getJSONObject(0).getString("url");
                        Log.d("Album Data", albumType + " " + totalTracks + " " + trackName + " " + artistId + " " + albumCoverImageURL + " " + albumName);

                        Track newTrack = new Track(artistId, trackName, artistName, albumType, albumCoverImageURL, albumName, trackId);
                        forYouTracks.add(newTrack);

                    }

                    completionListener.onComplete("Fetched tracks successfully");

                } catch (Exception e) {
                    Log.d("JSON FOR YOU", "Failed to parse data: " + e);
                }
            }
        });

        return null;
    }

    public void getUserProfile(Activity activity, CompletionListener loadedUserCompletion) {
        if (mAccessToken == null) {
            Toast.makeText(context, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                if (activity instanceof ProfileActivity) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(context, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException("Failed to fetch user profile data"));
                    return;
                }
                String responseString = response.body().string();

//                if (activity instanceof ProfileActivity) {
//                    activity.runOnUiThread(() -> {
//                        ((ProfileActivity) activity).updateProfileViews(responseString);
//                    });
//                }
                try {

                    /*String responseString = response.body().string();*/

                    Log.d("Spotify Data", responseString);

                    user = new SpotifyUser();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
                    user.populateUserData(responseString, mAuth.getUid(), loadedUserCompletion);

//                    String[] timeRanges = new String[]{"short_term", "medium_term", "long_term"};
//
//                    for (String timeRange : timeRanges) {
//                        fetchTopArtists(SpotifyManager.TopItemType.artists, timeRange, 20, new CompletionListener() {
//                            @Override
//                            public void onComplete(String result) throws IOException {
//                                Log.d("Size!! " + timeRange, timeRange);
//                                Log.d("URL!! " + timeRange, timeRange + " URL");
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
//                    }
//
//                    for (String timeRange : timeRanges) {
//                        fetchTopTracks(SpotifyManager.TopItemType.tracks, timeRange, 20, new CompletionListener() {
//                            @Override
//                            public void onComplete(String result) throws IOException {
//                                Log.d("Size!!", timeRange);
//                                if (timeRange.equals("long_term")) {
//                                    forYouArtists(new CompletionListener() {
//                                        @Override
//                                        public void onComplete(String result) throws IOException {
//                                            Log.d("SUCCESS", "Size: " + forYouTracks.size());
//                                        }
//
//                                        @Override
//                                        public void onError(Exception e) {
//
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }});
//                    }

                } catch (Exception e) {
                    Log.d("JSON USER PROFILE", "Failed to parse data: " + e);
//                    Toast.makeText(SpotifyLoginActivity.this, "Failed to parse data, watch Logcat for more details",
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void startActivity(Intent intent, Activity activity) {
        if (context != null) {
            Log.d("Reached Here", "REACHED");
            activity.startActivity(intent);
            activity.finish();
        }
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private static Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private static void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
