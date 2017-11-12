package com.ylimielinen.projectstudentnote.db.async.student;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.List;

/**
 * Created by decai on 12.11.2017.
 */

public class GetAllMarksOfStudent extends AsyncTask<String, Void, List<MarkEntity>> {

    private Context mContext;

    public GetAllMarksOfStudent(Context context) {
        mContext = context;
    }

    @Override
    protected List<MarkEntity> doInBackground(String... strings) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().markDao().getMarksOfStudentSync(strings[0]);
    }
}
