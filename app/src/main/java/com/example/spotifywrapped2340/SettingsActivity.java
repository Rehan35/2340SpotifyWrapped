package com.example.spotifywrapped2340;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmNewPasswordEditText;
    private Button updateButton, backButton, deleteAccountButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupButtonListeners();
    }

    private void initializeViews() {
        newPasswordEditText = findViewById(R.id.update_password);
        confirmNewPasswordEditText = findViewById(R.id.confirm_new_password);
        updateButton = findViewById(R.id.update_button);
        backButton = findViewById(R.id.back_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        logoutButton = findViewById(R.id.logout_button);
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> finish());

        updateButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmNewPassword = confirmNewPasswordEditText.getText().toString().trim();
            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (newPassword.isEmpty()) {
                Toast.makeText(this, "Enter a password!", Toast.LENGTH_SHORT).show();
            } else {
                updatePassword(newPassword);
            }
        });

        deleteAccountButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to update password", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete your account?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteAccount())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    logoutUser();
                } else {
                    Toast.makeText(SettingsActivity.this, "Failed to delete account", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void logoutUser() {
        clearRememberMePreferences();
        FirebaseAuth.getInstance().signOut();
        navigateToLoginActivity("Logged out successfully");
    }

    private void clearRememberMePreferences() {
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();
    }

    private void navigateToLoginActivity(String message) {
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
