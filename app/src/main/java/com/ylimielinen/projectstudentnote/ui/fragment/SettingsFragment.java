package com.ylimielinen.projectstudentnote.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.ui.activity.MainActivity;
import com.ylimielinen.projectstudentnote.util.Utils;


/**
 * Created by kb on 03.11.2017.
 * Settings fragment where we can modify our preference for the app
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);
    }

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
            // Restart the activity to make theme change happens
            Intent intent = getActivity().getIntent();
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void updateLanguage(Context baseContext, String lang) {
        // Update of the language
        if (Utils.updateLanguage(baseContext, lang)) {
            // Restart the activity to make changes happen
            Intent intent = getActivity().getIntent();
            startActivity(intent);
            getActivity().finish();
        }
    }
}