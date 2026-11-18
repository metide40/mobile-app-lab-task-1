package com.example.lab_exercises; // ← Change to your real package name

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Open Create Account screen when app starts
        startActivity(new Intent(this, CreateAccountActivity.class));
        finish(); // close MainActivity
    }
}