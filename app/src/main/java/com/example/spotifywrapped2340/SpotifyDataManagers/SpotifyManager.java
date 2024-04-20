package com.example.spotifywrapped2340.SpotifyDataManagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.spotifywrapped2340.Firebase.FirebaseManager;
import com.example.spotifywrapped2340.ObjectStructures.Artist;
import com.example.spotifywrapped2340.ObjectStructures.SpotifyUser;
import com.example.spotifywrapped2340.ProfileActivity;
import com.example.spotifywrapped2340.util.CompletionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
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
    private static String mAccessToken = "BQAKcHbszfzRFDaaUkRpP0g6QASYEAa0Exz7NoCowFrjmFwAij0wsQ4V359JsPq0TtzeOoq0aDdHIrQQsEiapzx2p3NOhczUwYRI_gl4pU1ahZ2efWO3Y5tG5b0mpVglnBuWZzs8BQau3FumeCERV0J2ZNZ_gikYcGI_RP3wsS-rKARI46o3ZLeBTypqYgT3SPCg7DbTou2zwF418HaH_6fXEtKo1pVYl77Jqz8rx7Rc4H0";

    private static String mAccessCode;
    private final static OkHttpClient mOkHttpClient = new OkHttpClient();
    private static Call mCall;
    private static SpotifyManager instance;
    private static Context context;

    public static ArrayList<Artist> topArtists = new ArrayList<>();

    private static Map<String, Integer> genreMap;

    private SpotifyManager(Context context) {
        this.context = context.getApplicationContext(); // Use application context
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
                .setScopes(new String[] { "user-read-email", "streaming", "playlist-read-private", "playlist-read-collaborative", "user-follow-read", "user-top-read", "user-library-read", }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    public static enum TopItemType {
        artists,
        tracks
    }

    public static ArrayList<Artist> fetchTopTracks(TopItemType type, String time_range, int limit) {
        ArrayList<Artist> artistsList = new ArrayList<Artist>();
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/" + type.toString())
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
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

                        JSONArray images = album.getJSONArray("images");
                        String albumCoverImageURL = images.getJSONObject(0).getString("url");
                        Log.d("Album Data", albumType + " " + totalTracks + " " + name + " " + artistId + " " + albumCoverImageURL);
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

    public static void fetchTopArtists(TopItemType type, String time_range, int limit, CompletionListener listener) {
        Log.d("HELLO", "WORLD");
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/" + type.toString())
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
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

    public static void getUserProfile(Activity activity) {
        if (mAccessToken == null) {
            Toast.makeText(context, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
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
                try {

                    String responseString = response.body().string();

                    Log.d("Spotify Data", responseString);

                    SpotifyUser user = new SpotifyUser();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    user.populateUserData(responseString, mAuth.getUid());
                    FirebaseManager.getInstance(context).populateUserSpotifyData(user);

                    Intent intent = new Intent(context, ProfileActivity.class);
                    startActivity(intent, activity);
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
