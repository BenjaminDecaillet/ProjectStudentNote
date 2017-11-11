package com.ylimielinen.projectstudentnote.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ylimielinen.projectstudentnote.R;

/**
 * Created by kb on 11.11.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("ACTIVITY BASE", "COUCOU CREATE");
        setTheme();
        super.onCreate(savedInstanceState);
    }

    private void setTheme(){
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString("Theme", "blue");
        Log.i("ACTIVITY BASE", "COUCOU");
        switch(theme){
            case "red":
                getBaseContext().setTheme(R.style.AppTheme_Red);
            default:
            case "blue":
                setTheme(R.style.AppTheme);
        }
    }
}
