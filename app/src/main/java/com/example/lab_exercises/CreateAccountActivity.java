package com.example.lab_exercises;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilEmail, tilAge, tilPassword;
    private TextInputEditText etFullName, etEmail, etAge, etPassword;
    private RadioGroup rgGender;
    private CheckBox cbTerms;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Find TextInputLayouts
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilAge = findViewById(R.id.tilAge);
        tilPassword = findViewById(R.id.tilPassword);

        // Find EditTexts
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etAge = findViewById(R.id.etAge);
        etPassword = findViewById(R.id.etPassword);

        // Find other views
        rgGender = findViewById(R.id.rgGender);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Automatically clear validation errors when user types
        setupErrorClearing();

        // Register button click
        btnRegister.setOnClickListener(v -> validateAndRegister());

        // Already have an account? Go to Login
        if (tvGoToLogin != null) {
            tvGoToLogin.setOnClickListener(v -> {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                finish();
            });
        }
    }

    private void setupErrorClearing() {
        clearErrorOnType(etFullName, tilFullName);
        clearErrorOnType(etEmail, tilEmail);
        clearErrorOnType(etAge, tilAge);
        clearErrorOnType(etPassword, tilPassword);
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

    private void validateAndRegister() {
        // Clear previous error messages
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilAge.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String ageStr = etAge.getText() != null ? etAge.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        // 1. Name verification: cannot be empty, must NOT contain any numbers, letters/spaces only
        if (name.isEmpty()) {
            tilFullName.setError("Please enter your full name");
            isValid = false;
        } else if (name.matches(".*\\d.*")) {
            tilFullName.setError("Name cannot contain numbers");
            isValid = false;
        } else if (!name.matches("^[a-zA-Z\\s'\\-.]+$")) {
            tilFullName.setError("Name can only contain letters");
            isValid = false;
        } else if (name.length() < 2) {
            tilFullName.setError("Name must be at least 2 characters");
            isValid = false;
        }

        // 2. Email verification: cannot be empty, must match valid email format
        if (email.isEmpty()) {
            tilEmail.setError("Please enter your email address");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        // 3. Age verification: integer only, range 0 to 150
        if (ageStr.isEmpty()) {
            tilAge.setError("Please enter your age");
            isValid = false;
        } else {
            try {
                int age = Integer.parseInt(ageStr);
                if (age < 0 || age > 150) {
                    tilAge.setError("Age must be between 0 and 150");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilAge.setError("Age must be a valid integer");
                isValid = false;
            }
        }

        // 4. Password verification: cannot be empty, minimum 6 characters
        if (password.isEmpty()) {
            tilPassword.setError("Please enter a password");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // 5. Gender verification
        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // 6. Terms & conditions checkbox
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // If any validation failed, stop here
        if (!isValid) {
            return;
        }

        // Save registered user credentials
        SharedPreferences prefs = getSharedPreferences(LabApplication.PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(LabApplication.KEY_USER_NAME, name)
                .putString(LabApplication.KEY_USER_EMAIL, email)
                .putString(LabApplication.KEY_USER_PASSWORD, password)
                .apply();

        // Success message
        Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show();

        // Navigate to LoginActivity (not directly to Settings)
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.putExtra("REGISTERED_EMAIL", email);
        startActivity(intent);
        finish(); // Close CreateAccount screen
    }
}
