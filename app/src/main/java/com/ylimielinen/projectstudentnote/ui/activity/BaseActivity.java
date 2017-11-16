package com.ylimielinen.projectstudentnote.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.ylimielinen.projectstudentnote.R;


/**
 * Created by kb on 11.11.2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setStyle(boolean actionBar) {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString("Theme", "blue");
        switch (theme) {
            case "red":
                if (actionBar)
                    setTheme(R.style.AppTheme_Red);
                else
                    setTheme(R.style.AppTheme_Red_NoActionBar);

                break;
            case "yellow":
                if (actionBar)
                    setTheme(R.style.AppTheme_Yellow);
                else
                    setTheme(R.style.AppTheme_Yellow_NoActionBar);
                break;
            case "green":
                if (actionBar)
                    setTheme(R.style.AppTheme_Green);
                else
                    setTheme(R.style.AppTheme_Green_NoActionBar);
                break;
            default:
            case "blue":
                if (actionBar)
                    setTheme(R.style.AppTheme);
                else
                    setTheme(R.style.AppTheme_NoActionBar);
                break;
        }
    }

    public void setDrawerBackground(){
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString("Theme", "blue");

        View headerLayout = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        switch(theme){
            case "red":
                headerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkRed));
                break;
            case "yellow":
                headerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkYellow));
                break;
            case "green":
                headerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
                break;
            default:
            case "blue":
                headerLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
        }


    }
}