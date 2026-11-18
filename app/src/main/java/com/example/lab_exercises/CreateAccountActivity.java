package com.example.lab_exercises; // ← Change this to your real package name

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etEmail, etAge, etPassword;
    private RadioGroup rgGender;
    private CheckBox cbNews, cbTerms;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Find views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        etPassword = findViewById(R.id.etPassword);
        rgGender = findViewById(R.id.rgGender);
        cbNews = findViewById(R.id.cbNews);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);

        // Register button click
        btnRegister.setOnClickListener(v -> {
            if (!cbTerms.isChecked()) {
                Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || age.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Success message
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();

            // Go to Settings screen
            Intent intent = new Intent(CreateAccountActivity.this, SettingsActivity.class);
            intent.putExtra("USER_NAME", name);
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
            finish(); // Close this screen
        });
    }
}