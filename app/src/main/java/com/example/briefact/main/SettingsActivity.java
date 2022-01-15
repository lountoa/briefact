package com.example.briefact.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.briefact.splash.ui.SplashActivity;
import com.example.briefact.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import com.example.briefact.R;

import java.util.Objects;


/**
 * Settings for App: I think all codes are self explanatory
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Log.d("Сейчас в", "SettingsFragment");

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

            SwitchPreference enableMultipleLang = findPreference(getString(R.string.key_enable_multiple_lang));
            ListPreference listPreference = findPreference(getString(R.string.key_language_for_tesseract));
            MultiSelectListPreference multiSelectListPreference = findPreference(getString(R.string.key_language_for_tesseract_multi));
            Preference logOut = findPreference(getString(R.string.log_out));

            Preference emailContent = findPreference(getString(R.string.key_account_email_content));

            if (emailContent != null) {
                emailContent.setSummary(email);
            }

            logOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getActivity(), getString(R.string.loged_out),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            });

            if (enableMultipleLang.isChecked()) {
                multiSelectListPreference.setVisible(true);
                listPreference.setVisible(false);
            } else {
                multiSelectListPreference.setVisible(false);
                listPreference.setVisible(true);
            }

            enableMultipleLang.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean b = (boolean) newValue;
                multiSelectListPreference.setVisible(b);
                listPreference.setVisible(!b);

                return true;
            });

        }


    }
}