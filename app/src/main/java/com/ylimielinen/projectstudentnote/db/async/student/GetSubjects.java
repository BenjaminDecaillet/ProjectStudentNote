package com.ylimielinen.projectstudentnote.db.async.student;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.List;

/**
 * Created by decai on 11.11.2017.
 * Get all subjects of a student
 * Param = email Student
 */

public class GetSubjects extends AsyncTask<String, Void, List<SubjectEntity>> {

    private Context mContext;

    public GetSubjects(Context context) {
        mContext = context;
    }

    @Override
    protected List<SubjectEntity> doInBackground(String... strings) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().subjectDao().getSubjectOfStudentSynch(strings[0]);
    }
}
