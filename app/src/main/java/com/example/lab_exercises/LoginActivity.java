package com.example.lab_exercises;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilLoginEmail, tilLoginPassword;
    private TextInputEditText etLoginEmail, etLoginPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences(LabApplication.PREFS_NAME, MODE_PRIVATE);

        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Pre-fill registered email if passed via Intent
        String passedEmail = getIntent().getStringExtra("REGISTERED_EMAIL");
        if (passedEmail != null && !passedEmail.isEmpty()) {
            etLoginEmail.setText(passedEmail);
        } else {
            String savedEmail = prefs.getString(LabApplication.KEY_USER_EMAIL, "");
            if (!savedEmail.isEmpty()) {
                etLoginEmail.setText(savedEmail);
            }
        }

        boolean remember = prefs.getBoolean(LabApplication.KEY_REMEMBER_LOGIN, true);
        cbRememberMe.setChecked(remember);

        // Real-time error clearing
        clearErrorOnType(etLoginEmail, tilLoginEmail);
        clearErrorOnType(etLoginPassword, tilLoginPassword);

        // Login button
        btnLogin.setOnClickListener(v -> performLogin());

        // Go to Register screen
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            finish();
        });
    }

    private void clearErrorOnType(TextInputEditText editText, TextInputLayout inputLayout) {
        if (editText != null && inputLayout != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void performLogin() {
        tilLoginEmail.setError(null);
        tilLoginPassword.setError(null);

        String email = etLoginEmail.getText() != null ? etLoginEmail.getText().toString().trim() : "";
        String password = etLoginPassword.getText() != null ? etLoginPassword.getText().toString() : "";

        boolean isValid = true;

        if (email.isEmpty()) {
            tilLoginEmail.setError("Please enter your email address");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilLoginEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilLoginPassword.setError("Please enter your password");
            isValid = false;
        } else if (password.length() < 6) {
            tilLoginPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Verify credentials with registered account in SharedPreferences
        String registeredEmail = prefs.getString(LabApplication.KEY_USER_EMAIL, "");
        String registeredPassword = prefs.getString(LabApplication.KEY_USER_PASSWORD, "");
        String registeredName = prefs.getString(LabApplication.KEY_USER_NAME, "Olie");

        if (!registeredEmail.isEmpty() && (!email.equalsIgnoreCase(registeredEmail) || !password.equals(registeredPassword))) {
            tilLoginPassword.setError("Incorrect email or password");
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save login state and remember-me preference
        prefs.edit()
                .putBoolean(LabApplication.KEY_IS_LOGGED_IN, true)
                .putBoolean(LabApplication.KEY_REMEMBER_LOGIN, cbRememberMe.isChecked())
                .apply();

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

        // Navigate to SettingsActivity
        Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
        intent.putExtra("USER_NAME", !registeredName.isEmpty() ? registeredName : "Olie");
        intent.putExtra("USER_EMAIL", email);
        startActivity(intent);
        finish(); // Close LoginActivity
    }
}
