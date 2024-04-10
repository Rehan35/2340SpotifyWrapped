package com.example.spotifywrapped2340;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapped2340.Firebase.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView signUpEmail;
    TextView signUpPassword;
    TextView signUpConfirmPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login_page);
////        FirebaseApp.initializeApp(this);
//        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
        Button signInButton = (Button) findViewById(R.id.login_button);

        signUpEmail = (TextView) findViewById(R.id.signup_email);
        signUpPassword = (TextView) findViewById(R.id.signup_password);
        signUpConfirmPassword = (TextView) findViewById(R.id.confirm_password);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, password, confirmPassword;
                email = String.valueOf(signUpEmail.getText());
                password = String.valueOf(signUpPassword.getText());
                confirmPassword = String.valueOf(signUpConfirmPassword.getText());

                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter an Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter an Password", Toast.LENGTH_SHORT).show();

                } else if (confirmPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Confirm your Password", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(MainActivity.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseManager.getInstance(getApplicationContext()).addUserToDatabase(mAuth.getUid());
                                        Intent intent = new Intent(getApplicationContext(), SpotifyLoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(MainActivity.this, "Failed to Create Account. Try Again, or try logging in.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });


    }

//    /**
//     * Get token from Spotify
//     * This method will open the Spotify login activity and get the token
//     * What is token?
//     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
//     */
//    public void getToken() {
//        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
//        try {
//            AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
//        } catch (Exception e) {
//            Log.d("ERROR", e.toString());
//        }
//    }
//
//    /**
//     * Get code from Spotify
//     * This method will open the Spotify login activity and get the code
//     * What is code?
//     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
//     */
//    public void getCode() {
//        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
//        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
//    }
//
//
//    /**
//     * When the app leaves this activity to momentarily get a token/code, this function
//     * fetches the result of that external activity to get the response from Spotify
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
//
//
//
//        // Check which request code is present (if any)
//        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
//            if (response == null) {
//                Log.d("FAILURE", "response is null");
//            } else if (response.getAccessToken() == null) {
//                Log.d("FAILURE", "access token is null");
//            } else {
//                Intent intent = new Intent(MainActivity.this, SpotifyLoginActivity.class);
//                startActivity(intent);
//                Log.d("SUCCESS", response.getAccessToken());
//            }
////            mAccessToken = response.getAccessToken();
////            setTextAsync(mAccessToken, tokenTextView);
//
//        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
////            mAccessCode = response.getCode();
////            setTextAsync(mAccessCode, codeTextView);
//        }
//    }
//
//    /**
//     * Get user profile
//     * This method will get the user profile using the token
//     */
//    public void onGetUserProfileClicked() {
//        if (mAccessToken == null) {
//            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a request to get the user profile
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
//                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    final JSONObject jsonObject = new JSONObject(response.body().string());
//                    setTextAsync(jsonObject.toString(3), profileTextView);
//                } catch (JSONException e) {
//                    Log.d("JSON", "Failed to parse data: " + e);
//                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    /**
//     * Creates a UI thread to update a TextView in the background
//     * Reduces UI latency and makes the system perform more consistently
//     *
//     * @param text the text to set
//     * @param textView TextView object to update
//     */
//    private void setTextAsync(final String text, TextView textView) {
//        runOnUiThread(() -> textView.setText(text));
//    }
//
//    /**
//     * Get authentication request
//     *
//     * @param type the type of the request
//     * @return the authentication request
//     */
//    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
//        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
//                .setShowDialog(false)
//                .setScopes(new String[] { "user-read-email" }) // <--- Change the scope of your requested token here
//                .setCampaign("your-campaign-token")
//                .build();
//    }
//
//    /**
//     * Gets the redirect Uri for Spotify
//     *
//     * @return redirect Uri object
//     */
//    private Uri getRedirectUri() {
//        return Uri.parse(REDIRECT_URI);
//    }
//
//    private void cancelCall() {
//        if (mCall != null) {
//            mCall.cancel();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        cancelCall();
//        super.onDestroy();
//    }
}