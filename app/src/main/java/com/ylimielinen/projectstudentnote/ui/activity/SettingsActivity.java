package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.ylimielinen.projectstudentnote.R;

import android.util.Log;
import android.view.View;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_activity_settings);

        // Action bar for settings
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.app_preferences);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Register the change preference listener
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            // Change the language
            if(s.equals("Language")){
                // Get language defined in the app
                String lang = sharedPreferences.getString(MainActivity.PREFS_LNG, "en");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    updateLanguage(getContext(), lang);
                } else {
                    updateLanguage(getActivity().getBaseContext(), lang);
                }
            }

            if(s.equals("Theme")){
                // Change app theme
                String theme = sharedPreferences.getString(MainActivity.PREFS_THEME, "blue");
                getActivity().finish();
                final Intent intent = getActivity().getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
            }

        }

        private void updateLanguage(Context baseContext, String lang) {
            // Update of the language
            if (!lang.isEmpty() && baseContext != null) {
                // Create a new locale and set it as default
                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                baseContext.getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                // Restart the activity to make changes happen
                Intent intent = getActivity().getIntent();
                startActivity(intent);
                getActivity().finish();
            }
        }
    }
}