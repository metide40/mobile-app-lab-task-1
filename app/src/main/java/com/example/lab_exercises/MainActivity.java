package com.example.lab_exercises;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(LabApplication.PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(LabApplication.KEY_IS_LOGGED_IN, false);
        boolean rememberLogin = prefs.getBoolean(LabApplication.KEY_REMEMBER_LOGIN, false);

        if (isLoggedIn && rememberLogin) {
            // User is already logged in with "Remember my login"
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            // Default to Login screen
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish(); // close MainActivity
    }
}
