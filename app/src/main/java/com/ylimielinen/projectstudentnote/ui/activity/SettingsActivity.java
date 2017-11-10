package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.ylimielinen.projectstudentnote.R;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.title_activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        public static final String KEY_GET_LANGUAGE_SETTING = "languageSettings";

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

            SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);

            // Preferences
            String lang = pref.getString(MainActivity.PREFS_LNG, "en");
            ListPreference langPref = (ListPreference) findPreference("Language");
            if(langPref.getValue() == null){
                //TODO: Get this working. :-)
                if (lang.equals("en")) {
                    langPref.setValueIndex(0);
                } else {
                    langPref.setValueIndex(1);
                }
            }
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            String lang = sharedPreferences.getString(MainActivity.PREFS_LNG, "en");
            Log.i(TAG, "changed language to " + lang);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                updateLanguage(getContext(), lang);
            } else {
                updateLanguage(getActivity().getBaseContext(), lang);
            }
        }

        private void updateLanguage(Context baseContext, String lang) {
            Log.i(TAG, "update language to " + lang);
            if (!lang.isEmpty() && baseContext != null) {
                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                baseContext.getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                Intent intent = getActivity().getIntent();
                startActivity(intent);
                getActivity().finish();
            }
        }
    }
}
