package com.example.spotifywrapped2340;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped2340.Firebase.FirebaseManager;
import com.example.spotifywrapped2340.ObjectStructures.SpotifyUser;
import com.example.spotifywrapped2340.SpotifyDataManagers.JsonReader;
import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpotifyLoginActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "3f2eac4dbbb0498194d8b5d955949c1a";
    public static final String REDIRECT_URI = "spotify-wrapped-2340://auth";
    private String mAccessToken, mAccessCode;

    private FirebaseAuth mAuth;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private SpotifyManager spotifyManager;
    private Call mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_spotify);

        mAuth = FirebaseAuth.getInstance();
        spotifyManager = SpotifyManager.getInstance(getApplicationContext());

        TextView wrappedTextView = (TextView) findViewById(R.id.wrapped_text);
        Button connectWithSpotify = (Button) findViewById(R.id.connect_with_spotify_button);
        connectWithSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken();

            }
        });
    }

    public void getToken() {
        final AuthorizationRequest request = SpotifyManager.getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        try {
            AuthorizationClient.openLoginActivity(SpotifyLoginActivity.this, 0, request);
        } catch (Exception e) {
            Log.d("TOKEN ERROR", e.toString());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (response == null) {
            Log.d("TOKEN FAILURE", "response is null");
        } else if (response.getAccessToken() == null) {
            Log.d("TOKEN FAILURE", "access token is null");
        } else {
//            Intent intent = new Intent(SpotifyLoginActivity.this, SpotifyLoginActivity.class);
//            startActivity(intent);
            Log.d("TOKEN SUCCESS", response.getAccessToken());
            SpotifyManager.setAccessToken(response.getAccessToken());
            SpotifyManager.getInstance(getApplicationContext()).getUserProfile(this);
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
    }

//    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
//        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
//                .setShowDialog(false)
//                .setScopes(new String[] { "user-read-email", "streaming", "playlist-read-private", "playlist-read-collabortive", "user-follow-read", "user-top-read", "user-library-read", }) // <--- Change the scope of your requested token here
//                .setCampaign("your-campaign-token")
//                .build();
//    }
//
//    public void getUserProfile() {
//        if (mAccessToken == null) {
//            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final Request request = new Request.Builder()
//                .url("https://api.spotify.com/v1/me")
//                .addHeader("Authorization", "Bearer " + mAccessToken)
//                .build();
//
//        cancelCall();
//        mCall = mOkHttpClient.newCall(request);
//
//        mCall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("HTTP", "Failed to fetch data: " + e);
//                Toast.makeText(SpotifyLoginActivity.this, "Failed to fetch data, watch Logcat for more details",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//
//                    String responseString = response.body().string();
//
//                    Log.d("Spotify Data", responseString);
//
//                    SpotifyUser user = new SpotifyUser();
//                    user.populateUserData(responseString, mAuth.getUid());
//                    FirebaseManager.getInstance(getApplicationContext()).populateUserSpotifyData(user);
//
//                    Intent intent = new Intent(getApplicationContext(), WrappedDataActivity.class);
//                    startActivity(intent);
//                    finish();
//                } catch (Exception e) {
//                    Log.d("JSON", "Failed to parse data: " + e);
////                    Toast.makeText(SpotifyLoginActivity.this, "Failed to parse data, watch Logcat for more details",
////                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}
