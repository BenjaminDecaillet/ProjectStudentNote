package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.ui.fragment.LoginFragment;
import com.ylimielinen.projectstudentnote.ui.fragment.RegisterFragment;
import com.ylimielinen.projectstudentnote.util.Utils;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivity {
    private final String BACK_STACK_ROOT_TAG = "LOGIN_ACTIVITY";
    private DatabaseCreator db;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStyle(true);
        super.onCreate(savedInstanceState);

        // Load language
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lang = prefs.getString("Language", "en");
        Utils.updateLanguage(getApplicationContext(), lang);

        setContentView(R.layout.activity_login);

        final DatabaseCreator databaseCreator = DatabaseCreator.getInstance(this.getApplication());
        databaseCreator.createDb(this.getApplication());

        fragmentManager.beginTransaction().replace(R.id.flContentLogin, LoginFragment.newInstance(), BACK_STACK_ROOT_TAG).commit();
    }

    @Override
    public void onBackPressed() {
        // Return to previous fragment
        FragmentManager fm = getSupportFragmentManager();


        if(fm.getBackStackEntryCount() > 0){
            fm.popBackStack();
        } else{
            super.onBackPressed();
        }
    }
}