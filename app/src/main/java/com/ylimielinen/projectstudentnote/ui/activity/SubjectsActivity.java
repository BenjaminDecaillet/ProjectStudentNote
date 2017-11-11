package com.ylimielinen.projectstudentnote.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.student.GetStudent;
import com.ylimielinen.projectstudentnote.db.async.student.GetSubjects;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;
import com.ylimielinen.projectstudentnote.ui.adaptater.SubjectAdaptater;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SubjectsActivity extends BaseActivity {

    private static final String TAG = "SubjectsActivity";

    ListView oSubjectList;
    private StudentEntity studentLoggedIn;
    private String loggedInEmail;
    private List<SubjectEntity> cSubjectsOfStudent;

    private List<String> subjects = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init activity
        setStyle(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        setTitle(R.string.title_activity_subjects);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        oSubjectList = (ListView) findViewById(R.id.listSubject);

        displaySubjectList();

        oSubjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get Subject Clicked
                SubjectEntity subjectClicked = (SubjectEntity) parent.getItemAtPosition(position);
                Intent intent = new Intent(SubjectsActivity.this, MarkActivity.class);
//                intent.putExtra("position", position);
                intent.putExtra("idSubject", subjectClicked.getIdSubject());
                startActivity(intent);
            }
        });

    }

    private void displaySubjectList(){
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        loggedInEmail = settings.getString(MainActivity.PREFS_USER, null);

        try {
            studentLoggedIn = new GetStudent(getApplicationContext()).execute(loggedInEmail).get();
            cSubjectsOfStudent = new GetSubjects(getApplicationContext()).execute(loggedInEmail).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

//        for (SubjectEntity s : cSubjectsOfStudent) {
//            subjects.add(s.getName());
//        }
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SubjectsActivity.this,
//                android.R.layout.simple_list_item_1, subjects);
//        oSubjectList.setAdapter(adapter);

        SubjectAdaptater adapter = new SubjectAdaptater(SubjectsActivity.this, cSubjectsOfStudent);
        oSubjectList.setAdapter(adapter);
    }

}
