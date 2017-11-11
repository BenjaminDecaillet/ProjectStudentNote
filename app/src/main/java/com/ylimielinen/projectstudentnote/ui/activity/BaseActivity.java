package com.ylimielinen.projectstudentnote.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ylimielinen.projectstudentnote.R;


/**
 * Created by kb on 11.11.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setStyle(boolean actionBar){
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString("Theme", "blue");
        switch(theme){
            case "red":
                if(actionBar) {
                    setTheme(R.style.AppTheme_Red);
                }
                else
                    setTheme(R.style.AppTheme_Red_NoActionBar);
                break;
            case "yellow":
                if(actionBar) {
                    setTheme(R.style.AppTheme_Yellow);
                }
                else
                    setTheme(R.style.AppTheme_Yellow_NoActionBar);
                break;
            case "green":
                if(actionBar) {
                    setTheme(R.style.AppTheme_Green);
                }
                else
                    setTheme(R.style.AppTheme_Green_NoActionBar);
                break;
            default:
            case "blue":
                if(actionBar) {
                    setTheme(R.style.AppTheme);
                }
                else
                    setTheme(R.style.AppTheme_NoActionBar);
                break;
        }
    }
}