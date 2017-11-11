package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.async.student.GetSubjects;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SubjectsActivity extends BaseActivity {

    private static final String TAG = "SubjectsActivity";

    ListView oSubjectList;
    private StudentEntity studentLoggedIn;
    private String loggedInEmail;
    private List<SubjectEntity> cSubjectsOfStudent;

    private String[] prenoms = new String[]{
            "Antoine", "Benoit", "Cyril", "David", "Eloise", "Florent",
            "Gerard", "Hugo", "Ingrid", "Jonathan", "Kevin", "Logan",
            "Mathieu", "Noemie", "Olivia", "Philippe", "Quentin", "Romain",
            "Sophie", "Tristan", "Ulric", "Vincent", "Willy", "Xavier",
            "Yann", "Zoé"
    };
    private List<String> subjects = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStyle(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        setTitle(R.string.title_activity_subjects);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        loggedInEmail = settings.getString(MainActivity.PREFS_USER, null);

        try {
            studentLoggedIn = new GetStudent(getApplicationContext()).execute(loggedInEmail).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        oSubjectList = (ListView) findViewById(R.id.listSubject);

        try {
            cSubjectsOfStudent = new GetSubjects(getApplicationContext()).execute(loggedInEmail).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (SubjectEntity s : cSubjectsOfStudent) {
            subjects.add(s.getName());
        }



        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubjectsActivity.this,
                android.R.layout.simple_list_item_1, subjects);
        oSubjectList.setAdapter(adapter);
    }
}
