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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyManager {

    public static final String CLIENT_ID = "3f2eac4dbbb0498194d8b5d955949c1a";
    public static final String REDIRECT_URI = "spotify-wrapped-2340://auth";
    private static String mAccessToken = "BQCpOif0GgNuBhAKGREhNOMEDXVYG4dLo8p1WsAG3ddIODtyXrKjW1fr91aq2DTBIXfyj2l1FOr_hPacKlrYvUgTbVrBDrOt2d5G-ONWtYtBdqyH5ZOQ5fcYspFBVcp86pdygSBJ1aGzPGf4GMZxgL32RGKslukQLWlLVQ99JX52pJKTF-HM3PJ8Z5dFcasqPcioCneUDLMKBjFLK7HUHTW7KBse9VfH1EmmSPCMlpZCKS4";

    private static String mAccessCode;

    public SpotifyUser user;

    private final static OkHttpClient mOkHttpClient = new OkHttpClient();
    private static Call mCall;
    private static SpotifyManager instance;
    private static Context context;

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
                        int totalTracks = album.getInt("total_tracks");
//                        String name = album.getString("name");

                        JSONObject track = albums.getJSONObject(i);

                        String trackName = track.getString("name");


                        JSONArray artists = album.getJSONArray("artists");
                        String artistId = artists.getJSONObject(0).getString("id");
                        String artistName = artists.getJSONObject(0).getString("name");

                        JSONArray images = album.getJSONArray("images");
                        String albumCoverImageURL = images.getJSONObject(0).getString("url");
                        Log.d("Album Data", albumType + " " + totalTracks + " " + trackName + " " + artistId + " " + albumCoverImageURL);

                        if (time_range.equals("short_term")) {
                            topTracksShort.add(new Track(artistId, trackName, artistName, albumType, albumCoverImageURL));
                            topTracks = topTracksShort;
                        } else if (time_range.equals("medium_term")) {
                            topTracksMedium.add(new Track(artistId, trackName, artistName, albumType, albumCoverImageURL));
                            topTracks = topTracksMedium;
                        } else if (time_range.equals("long_term")) {
                            topTracksLong.add(new Track(artistId, trackName, artistName, albumType, albumCoverImageURL));
                            topTracks = topTracksLong;
                        }
                        //topTracks.add(new Track(artistId, name, artistName, albumType, albumCoverImageURL));
                    }
                } catch (Exception e) {
                    Log.d("JSON", "Failed to parse data: " + e);
//                    Toast.makeText(SpotifyLoginActivity.this, "Failed to parse data, watch Logcat for more details",
//                            Toast.LENGTH_SHORT).show();
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

//                        String url = imagesArray.getJSONObject(0).getString("url");
//                        topArtists.get(i).setArtistImageUrl(url);
//                        Log.d("URL", url);

                    }
                    listener.onComplete("Task completed successfully!");



//                    SpotifyUser user = new SpotifyUser();
//
//                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//                    user.populateUserData(responseString, mAuth.getUid());
//                    FirebaseManager.getInstance(context).populateUserSpotifyData(user);
//
//                    Intent intent = new Intent(context, ProfileActivity.class);
//                    startActivity(intent, activity);
                } catch (Exception e) {
                    Log.d("JSON", "Failed to parse data: " + e);
//                    Toast.makeText(SpotifyLoginActivity.this, "Failed to parse data, watch Logcat for more details",
//                            Toast.LENGTH_SHORT).show();
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


    public void getUserProfile(Activity activity) {
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
                Toast.makeText(context, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException("Failed to fetch user profile data"));
                    return;
                }
                String responseString = response.body().string();

                if (activity instanceof ProfileActivity) {
                    activity.runOnUiThread(() -> {
                        ((ProfileActivity) activity).updateProfileViews(responseString);
                    });
                }
                try {

                    /*String responseString = response.body().string();*/

                    Log.d("Spotify Data", responseString);

                    user = new SpotifyUser();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
                    user.populateUserData(responseString, mAuth.getUid());
//                    FirebaseManager.getInstance(context).populateUserSpotifyData(user);

                    fetchTopArtists(SpotifyManager.TopItemType.artists, "short_term", 20, new CompletionListener() {
                        @Override
                        public void onComplete(String result) throws IOException {
                            Log.d("Size!!", topArtistsShort.get(0).getName());
                            String name = topArtistsShort.get(0).getName();
                            String url = topArtistsShort.get(0).getArtistImageUrl();
                            Log.d("URL!!", url);

//                            Intent intent = new Intent(context, ProfileActivity.class);
//                            startActivity(intent, activity);

                            /*Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);*/
                            /*startActivity(intent);*/
                            /*finish();*/
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                    fetchTopArtists(SpotifyManager.TopItemType.artists, "medium_term", 20, new CompletionListener() {
                        @Override
                        public void onComplete(String result) throws IOException {
                            Log.d("Size!!", topArtistsMedium.get(0).getName());
                            String name = topArtistsMedium.get(0).getName();
                            String url = topArtistsMedium.get(0).getArtistImageUrl();
                            Log.d("URL!!", url);

//                            Intent intent = new Intent(context, ProfileActivity.class);
//                            startActivity(intent, activity);

                            /*Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);*/
                            /*startActivity(intent);*/
                            /*finish();*/
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                    fetchTopArtists(SpotifyManager.TopItemType.artists, "long_term", 20, new CompletionListener() {
                        @Override
                        public void onComplete(String result) throws IOException {
                            Log.d("Size!!", topArtistsLong.get(0).getName());
                            String name = topArtistsLong.get(0).getName();
                            String url = topArtistsLong.get(0).getArtistImageUrl();
                            Log.d("URL!!", url);

//                            Intent intent = new Intent(context, ProfileActivity.class);
//                            startActivity(intent, activity);

                            /*Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);*/
                            /*startActivity(intent);*/
                            /*finish();*/
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                    SpotifyManager.getInstance(context).fetchTopTracks(SpotifyManager.TopItemType.tracks, "short_term", 20, new CompletionListener() {
                        @Override
                        public void onComplete(String result) throws IOException {
                            Log.d("Size!!", SpotifyManager.getInstance(context).topTracksShort.size() + "");
//                String name = SpotifyManager.getInstance(getApplicationContext()).topTracks.get(0).;
//                String url = SpotifyManager.getInstance(getApplicationContext()).topArtists.get(0).getArtistImageUrl();
//                Log.d("URL!!", url);
                            /*Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);*/
                            /*startActivity(intent);*/
                            /*finish();*/
                        }

                        @Override
                        public void onError(Exception e) {

                        }});

                    SpotifyManager.getInstance(context).fetchTopTracks(SpotifyManager.TopItemType.tracks, "medium_term", 20, new CompletionListener() {
                        @Override
                        public void onComplete(String result) throws IOException {
                            Log.d("Size!!", SpotifyManager.getInstance(context).topTracksMedium.size() + "");
//                String name = SpotifyManager.getInstance(getApplicationContext()).topTracks.get(0).;
//                String url = SpotifyManager.getInstance(getApplicationContext()).topArtists.get(0).getArtistImageUrl();
//                Log.d("URL!!", url);
                            /*Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);*/
                            /*startActivity(intent);*/
                            /*finish();*/
                        }

                        @Override
                        public void onError(Exception e) {

                        }});

                    SpotifyManager.getInstance(context).fetchTopTracks(SpotifyManager.TopItemType.tracks, "long_term", 20, new CompletionListener() {
                        @Override
                        public void onComplete(String result) throws IOException {
                            Log.d("Size!!", SpotifyManager.getInstance(context).topTracksLong.size() + "");
//                String name = SpotifyManager.getInstance(getApplicationContext()).topTracks.get(0).;
//                String url = SpotifyManager.getInstance(getApplicationContext()).topArtists.get(0).getArtistImageUrl();
//                Log.d("URL!!", url);
                            /*Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);*/
                            /*startActivity(intent);*/
                            /*finish();*/
                        }

                        @Override
                        public void onError(Exception e) {

                        }});

                } catch (Exception e) {
                    Log.d("JSON", "Failed to parse data: " + e);
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
