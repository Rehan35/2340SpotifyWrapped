package com.example.spotifywrapped2340;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapped2340.SpotifyDataManagers.SpotifyManager;
import com.example.spotifywrapped2340.util.CompletionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    TextView signInEmail;
    TextView signInPassword;
    CheckBox rememberMe;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            /*SpotifyManager.getInstance(getApplicationContext()).fetchTopArtists(SpotifyManager.TopItemType.artists, "", 20, new CompletionListener() {
                @Override
                public void onComplete(String result) throws IOException {
                    Log.d("Size!!", SpotifyManager.getInstance(getApplicationContext()).topArtists.get(0).getName());
                    String name = SpotifyManager.getInstance(getApplicationContext()).topArtists.get(0).getName();
                    String url = SpotifyManager.getInstance(getApplicationContext()).topArtists.get(0).getArtistImageUrl();
                    Log.d("URL!!", url);
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(Exception e) {

                }
            });*/
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        rememberMe = findViewById(R.id.loginCheckBox);
        SharedPreferences pref = getSharedPreferences("checkbox", MODE_PRIVATE);
        String check = pref.getString("remember", "");
        if (check.equals("true")) {
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences pref = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                } else if (!buttonView.isChecked()) {
                    SharedPreferences pref = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                }
            }
        });

        signInEmail = findViewById(R.id.signin_email);
        signInPassword = findViewById(R.id.signin_password);

        Button loginButton = (Button) findViewById(R.id.signin_button);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(signInEmail.getText());
                password = String.valueOf(signInPassword.getText());

                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email Required", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password Required", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Successful Log In!", Toast.LENGTH_SHORT).show();
                                Log.d("USER ID", mAuth.getUid());
                                Intent intent = new Intent(getApplicationContext(), SpotifyLoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to Authenticate. Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        Button createAccountButton = (Button) findViewById(R.id.create_account_button);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}