package com.example.spotifywrapped2340;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {

    private EditText editTextNewEmail, editTextNewPassword;
    private Button buttonUpdateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextNewEmail = findViewById(R.id.editEmail);
        editTextNewPassword = findViewById(R.id.editPassword);
        buttonUpdateInfo = findViewById(R.id.buttonUpdateInfo);

        buttonUpdateInfo.setOnClickListener(view -> updateUserInfo());
    }

    private void updateUserInfo() {
        String newEmail = editTextNewEmail.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!newEmail.isEmpty()) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Settings.this, "Email updated", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (!newPassword.isEmpty()) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Settings.this, "Password updated", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}