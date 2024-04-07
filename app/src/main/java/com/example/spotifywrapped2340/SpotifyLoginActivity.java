package com.example.spotifywrapped2340;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class SpotifyLoginActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "3f2eac4dbbb0498194d8b5d955949c1a";
    public static final String REDIRECT_URI = "spotify-wrapped-2340://auth";
    private String mAccessToken, mAccessCode;
    private Call mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_spotify);
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();

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
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        try {
            AuthorizationClient.openLoginActivity(SpotifyLoginActivity.this, 0, request);
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (response == null) {
            Log.d("FAILURE", "response is null");
        } else if (response.getAccessToken() == null) {
            Log.d("FAILURE", "access token is null");
        } else {
            Intent intent = new Intent(SpotifyLoginActivity.this, SpotifyLoginActivity.class);
            startActivity(intent);
            Log.d("SUCCESS", response.getAccessToken());
        }
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

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
