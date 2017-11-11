package com.ylimielinen.projectstudentnote.ui.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.widget.ListView;

import com.ylimielinen.projectstudentnote.R;
import com.ylimielinen.projectstudentnote.db.async.subject.GetMarks;
import com.ylimielinen.projectstudentnote.db.async.subject.GetSubject;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;
import com.ylimielinen.projectstudentnote.ui.adapter.MarkAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MarkActivity extends BaseActivity {

    private static final String TAG = "MarksActivity";

    ListView oMarkList;
    private SubjectEntity currentSubject;
    private List<MarkEntity> cMarksOfSubject;
    private Long idSubject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init activity
        setStyle(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        setTitle(R.string.title_activity_marks);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            idSubject= null;
        } else {
            idSubject= extras.getLong("idSubject");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        oMarkList = (ListView) findViewById(R.id.listMarks);

        displayMarkList();
    }

    private void displayMarkList(){

        try {
            currentSubject = new GetSubject(getApplicationContext()).execute(idSubject).get();
            cMarksOfSubject = new GetMarks(getApplicationContext()).execute(idSubject).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        MarkAdapter adapter = new MarkAdapter(MarkActivity.this, cMarksOfSubject);
        oMarkList.setAdapter(adapter);
    }
}
