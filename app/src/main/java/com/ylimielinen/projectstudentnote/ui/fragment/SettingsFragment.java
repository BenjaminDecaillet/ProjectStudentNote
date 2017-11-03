package com.ylimielinen.projectstudentnote.ui.fragment;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.ylimielinen.projectstudentnote.R;

/**
 * Created by kb on 03.11.2017.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
