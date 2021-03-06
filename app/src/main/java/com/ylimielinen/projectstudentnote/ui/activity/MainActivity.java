package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.ui.fragment.HomeFragment;
import com.ylimielinen.projectstudentnote.ui.fragment.RegisterFragment;
import com.ylimielinen.projectstudentnote.ui.fragment.SettingsFragment;
import com.ylimielinen.projectstudentnote.ui.fragment.subject.SubjectsFragment;

import java.util.concurrent.ExecutionException;

/**
 * Main Activity with fragment Gestion and Main variants
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private final String BACK_STACK_ROOT_TAG = "MAIN";

    public static final String PREFS_NAME = "SharedPrefs";
    public static final String PREFS_USER = "LoggedIn";
    public static final String PREFS_ADM = "UserPermission";
    public static final String PREFS_LNG = "Language";

    private Boolean admin;
    private String loggedInEmail;
    private StudentEntity loggedIn;
    private Fragment fragment;
    private Class fragmentClass;
    FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStyle(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        loggedInEmail = settings.getString(MainActivity.PREFS_USER, null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (savedInstanceState == null) {
            fragment = null;
            fragmentClass = HomeFragment.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, BACK_STACK_ROOT_TAG).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        drawer.closeDrawer(GravityCompat.START);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initNavDrawerContent();
        setDrawerBackground();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.action_logout));
            alertDialog.setCancelable(false);
            alertDialog.setMessage(getString(R.string.logout_msg));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_logout), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
            return;
        }

        super.onBackPressed();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        String fragmentTag = "";

        // Define fragment according to pressed menu item
        if (id == R.id.nav_settings) {
            //Display settings
            fragmentClass = SettingsFragment.class;
            fragmentTag = "Settings";
        }else if(id == R.id.nav_logout){
            // Log user out
            logout();
            fragmentTag = "Logout";
        }else if (id == R.id.nav_userprofile){
            //Display Edit User
            fragmentClass = RegisterFragment.class;
            fragmentTag = "EditUser";
        }else if (id == R.id.nav_subjects){
            //Display List Subject
            fragmentClass = SubjectsFragment.class;
            fragmentTag = "Subjects";
        }else if (id == R.id.nav_home){
            //Display home screen
            fragmentClass = HomeFragment.class;
            fragmentTag = "Home";
        }

        try {
            if(fragmentTag.equals("EditUser"))
                fragment = RegisterFragment.newInstance(true);
            else
                fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(fragmentTag).commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get menu item clicked
        int id = item.getItemId();

        // open settings fragment
        if (id == R.id.action_settings) {
            setTitle(R.string.title_activity_settings);
            fragmentManager.beginTransaction().replace(R.id.flContent, SettingsFragment.newInstance()).addToBackStack("Settings").commit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != this.getCurrentFocus())
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    private void initNavDrawerContent(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        final View headerLayout = navigationView.getHeaderView(0);
        TextView studentEmail = headerLayout.findViewById(R.id.userEmail);
        TextView studentName = headerLayout.findViewById(R.id.userName);

        try {
            loggedIn = new GetStudent(getApplicationContext()).execute(loggedInEmail).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if(loggedIn != null) {
            studentEmail.setText(loggedIn.getEmail());
            studentName.setText(String.format("%s %s", loggedIn.getFirstName(), loggedIn.getLastName()));
        }
    }

    private void logout() {
        // Delete user informations
        SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
        editor.remove(PREFS_USER);
        editor.remove(PREFS_ADM);
        editor.apply();

        // Empty backstack if logout from another fragment than home
        int count = fragmentManager.getBackStackEntryCount();
        for(int i = 0; i < count; ++i) {
            fragmentManager.popBackStack();
        }

        // Start login activity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}