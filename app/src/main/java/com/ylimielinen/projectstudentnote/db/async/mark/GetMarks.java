package com.ylimielinen.projectstudentnote.db.async.mark;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

import java.util.List;

/**
 * Created by kb on 12.11.2017.
 */

public class GetMarks extends AsyncTask<Object, Void, List<MarkEntity>> {
    @Override
    protected List<MarkEntity> doInBackground(Object... params) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance((Context)params[0]);
        return dbCreator.getDatabase().markDao().getMarksOfSubjectByStudentSync((Long)params[1], (String)params[2]);
    }
}
