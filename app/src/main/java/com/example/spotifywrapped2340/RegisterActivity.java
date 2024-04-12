package com.example.spotifywrapped2340;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifywrapped2340.Firebase.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    TextView signUpEmail;
    TextView signUpPassword;
    TextView signUpConfirmPassword;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login_page);
        Button signInButton = (Button) findViewById(R.id.login_button);

        signUpEmail = (TextView) findViewById(R.id.signup_email);
        signUpPassword = (TextView) findViewById(R.id.signup_password);
        signUpConfirmPassword = (TextView) findViewById(R.id.confirm_password);

        Button alreadyHaveAccount = (Button) findViewById(R.id.have_account);

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, password, confirmPassword;
                email = String.valueOf(signUpEmail.getText());
                password = String.valueOf(signUpPassword.getText());
                confirmPassword = String.valueOf(signUpConfirmPassword.getText());

                if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter an Email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Enter an Password", Toast.LENGTH_SHORT).show();

                } else if (confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please Confirm your Password", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(RegisterActivity.this, "Failed to Create Account. Try Again, or try logging in.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });


    }
}