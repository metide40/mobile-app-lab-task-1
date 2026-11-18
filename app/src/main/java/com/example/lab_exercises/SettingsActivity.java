package com.example.lab_exercises;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {

    private static final String PROFILE_IMAGE_NAME = "profile_image.jpg";
    private static final String KEY_SOUND_ON = "sound_on";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_STATUS = "status";

    private ImageButton btnBack;
    private ImageView ivProfile;
    private View layoutProfile;
    private TextView tvName, tvEmail;
    private SwitchCompat switchDarkMode, switchNotifications;
    private Button btnSound, btnSave, btnReset;
    private CheckBox cbRemember;
    private RadioGroup rgLanguage;
    private EditText etStatus;
    private boolean soundOn = true;

    private SharedPreferences prefs;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(LabApplication.PREFS_NAME, MODE_PRIVATE);

        // Back button
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        // Register image picker contract
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        saveProfileImage(uri);
                        loadProfileImage(ivProfile);
                        Toast.makeText(this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Profile views
        ivProfile = findViewById(R.id.ivProfile);
        layoutProfile = findViewById(R.id.layoutProfile);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);

        if (ivProfile != null) {
            ivProfile.setClipToOutline(true);
            loadProfileImage(ivProfile);
            ivProfile.setOnClickListener(v -> showProfileImageDialog());
        }
        if (layoutProfile != null) {
            layoutProfile.setOnClickListener(v -> showProfileImageDialog());
        }

        // Restore user name & email
        if (savedInstanceState != null) {
            tvName.setText(savedInstanceState.getString("USER_NAME", "Olie"));
            tvEmail.setText(savedInstanceState.getString("USER_EMAIL", "oli@gmail.com"));
            soundOn = savedInstanceState.getBoolean("SOUND_ON", true);
        } else {
            String userName = getIntent().getStringExtra("USER_NAME");
            String userEmail = getIntent().getStringExtra("USER_EMAIL");
            if (userName == null || userName.isEmpty()) {
                userName = prefs.getString(LabApplication.KEY_USER_NAME, "Olie");
            }
            if (userEmail == null || userEmail.isEmpty()) {
                userEmail = prefs.getString(LabApplication.KEY_USER_EMAIL, "oli@gmail.com");
            }
            tvName.setText(userName);
            tvEmail.setText(userEmail);
        }

        // Settings views
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);
        btnSound = findViewById(R.id.btnSound);
        cbRemember = findViewById(R.id.cbRemember);
        rgLanguage = findViewById(R.id.rgLanguage);
        etStatus = findViewById(R.id.etStatus);
        btnSave = findViewById(R.id.btnSave);
        btnReset = findViewById(R.id.btnReset);

        // Initialize Dark Mode switch state
        boolean isDarkMode = prefs.getBoolean(LabApplication.KEY_DARK_MODE, false);
        switchDarkMode.setChecked(isDarkMode);

        // Dark mode toggle listener
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean currentSavedMode = prefs.getBoolean(LabApplication.KEY_DARK_MODE, false);
            if (currentSavedMode != isChecked) {
                prefs.edit().putBoolean(LabApplication.KEY_DARK_MODE, isChecked).apply();
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        // Initialize other preferences if saved
        boolean notifPref = prefs.getBoolean(KEY_NOTIFICATIONS, true);
        switchNotifications.setChecked(notifPref);

        soundOn = prefs.getBoolean(KEY_SOUND_ON, soundOn);
        btnSound.setText(soundOn ? "SOUND ON" : "SOUND OFF");

        boolean rememberPref = prefs.getBoolean(KEY_REMEMBER, true);
        cbRemember.setChecked(rememberPref);

        int langId = prefs.getInt(KEY_LANGUAGE, R.id.rbEnglish);
        rgLanguage.check(langId);

        String savedStatus = prefs.getString(KEY_STATUS, "");
        if (!savedStatus.isEmpty() && (etStatus.getText() == null || etStatus.getText().toString().isEmpty())) {
            etStatus.setText(savedStatus);
        }

        btnSound.setOnClickListener(v -> {
            soundOn = !soundOn;
            btnSound.setText(soundOn ? "SOUND ON" : "SOUND OFF");
        });

        btnSave.setOnClickListener(v -> {
            prefs.edit()
                    .putBoolean(LabApplication.KEY_DARK_MODE, switchDarkMode.isChecked())
                    .putBoolean(KEY_NOTIFICATIONS, switchNotifications.isChecked())
                    .putBoolean(KEY_SOUND_ON, soundOn)
                    .putBoolean(KEY_REMEMBER, cbRemember.isChecked())
                    .putInt(KEY_LANGUAGE, rgLanguage.getCheckedRadioButtonId())
                    .putString(KEY_STATUS, etStatus.getText().toString().trim())
                    .apply();
            Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
        });

        btnReset.setOnClickListener(v -> {
            boolean wasDarkMode = prefs.getBoolean(LabApplication.KEY_DARK_MODE, false);

            prefs.edit()
                    .putBoolean(LabApplication.KEY_DARK_MODE, false)
                    .putBoolean(KEY_NOTIFICATIONS, true)
                    .putBoolean(KEY_SOUND_ON, true)
                    .putBoolean(KEY_REMEMBER, true)
                    .putInt(KEY_LANGUAGE, R.id.rbEnglish)
                    .putString(KEY_STATUS, "")
                    .apply();

            switchNotifications.setChecked(true);
            soundOn = true;
            btnSound.setText("SOUND ON");
            cbRemember.setChecked(true);
            rgLanguage.check(R.id.rbEnglish);
            etStatus.setText("");

            // Reset profile picture to default
            File file = new File(getFilesDir(), PROFILE_IMAGE_NAME);
            if (file.exists()) {
                file.delete();
            }
            loadProfileImage(ivProfile);

            if (wasDarkMode) {
                switchDarkMode.setChecked(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                switchDarkMode.setChecked(false);
            }

            Toast.makeText(this, "Reset to default", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SOUND_ON", soundOn);
        if (tvName != null) {
            outState.putString("USER_NAME", tvName.getText().toString());
        }
        if (tvEmail != null) {
            outState.putString("USER_EMAIL", tvEmail.getText().toString());
        }
    }

    /**
     * Opens a popup dialog showing the enlarged profile image
     * with options to view or change it.
     */
    private void showProfileImageDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_profile_image);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        ImageView dialogIvProfile = dialog.findViewById(R.id.dialogIvProfile);
        ImageButton btnClose = dialog.findViewById(R.id.btnCloseDialog);
        Button btnChangePhoto = dialog.findViewById(R.id.btnChangePhoto);
        Button btnRemovePhoto = dialog.findViewById(R.id.btnRemovePhoto);

        // Load the current profile image into the dialog
        loadProfileImage(dialogIvProfile);

        File savedFile = new File(getFilesDir(), PROFILE_IMAGE_NAME);
        if (savedFile.exists()) {
            btnRemovePhoto.setVisibility(View.VISIBLE);
        } else {
            btnRemovePhoto.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnChangePhoto.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
            dialog.dismiss();
        });

        btnRemovePhoto.setOnClickListener(v -> {
            if (savedFile.exists()) {
                savedFile.delete();
            }
            loadProfileImage(ivProfile);
            Toast.makeText(this, "Profile image reset to default", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void loadProfileImage(ImageView targetImageView) {
        if (targetImageView == null) return;
        File file = new File(getFilesDir(), PROFILE_IMAGE_NAME);
        if (file.exists()) {
            targetImageView.setImageURI(null); // Invalidate cached drawable
            targetImageView.setImageURI(Uri.fromFile(file));
        } else {
            targetImageView.setImageResource(R.drawable.profile);
        }
    }

    private void saveProfileImage(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(new File(getFilesDir(), PROFILE_IMAGE_NAME))) {
            if (inputStream != null) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save profile image", Toast.LENGTH_SHORT).show();
        }
    }
}
