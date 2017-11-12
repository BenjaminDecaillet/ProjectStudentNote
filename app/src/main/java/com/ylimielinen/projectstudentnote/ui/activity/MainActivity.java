package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.ui.fragment.SettingsFragment;
import com.ylimielinen.projectstudentnote.ui.fragment.subject.SubjectsFragment;

import java.util.concurrent.ExecutionException;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";
    private final String BACK_STACK_ROOT_TAG = "MAIN";

    public static final String PREFS_NAME = "SharedPrefs";
    public static final String PREFS_USER = "LoggedIn";
    public static final String PREFS_ADM = "UserPermission";
    public static final String PREFS_LNG = "Language";
    public static final String PREFS_THEME = "Theme";

    private Boolean admin;
    private String loggedInEmail;
    private StudentEntity loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStyle(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        loggedInEmail = settings.getString(MainActivity.PREFS_USER, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initNavDrawerContent();
    }

    @Override
    public void onBackPressed() {
        // TODO: Avoid to re-open login screen and avoid to empty subjects list
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment fragment = null;
        Class fragmentClass = null;
        String fragmentTag = null;

        if (id == R.id.nav_settings) {
            /*Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;*/
            //SettingsFragment sFragment = SettingsFragment.newInstance();


            fragmentManager.beginTransaction().replace(R.id.flContent, new SettingsFragment()).addToBackStack(BACK_STACK_ROOT_TAG).commit();
        }else if(id == R.id.nav_logout){
            // Log user out
            logout();
        }else if (id == R.id.nav_subjects){
            //Display List Subject
            /*Intent intent = new Intent(this, SubjectsActivity.class);
            startActivity(intent);
            return true;*/
            fragmentClass = SubjectsFragment.class;
            fragmentTag = "subjects";

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(BACK_STACK_ROOT_TAG).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNavDrawerContent(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.getHeaderView(0);
        TextView studentEmail = (TextView) headerLayout.findViewById(R.id.userEmail);
        TextView studentName = (TextView) headerLayout.findViewById(R.id.userName);

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

        // Start login activity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}