package com.ylimielinen.projectstudentnote.db.async.subject;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

/**
 * Created by decai on 04.11.2017.
 */

public class CreateSubject extends AsyncTask<SubjectEntity, Void, Long> {

    private Context mContext;

    public CreateSubject(Context context) {
        mContext = context;
    }

    @Override
    protected Long doInBackground(SubjectEntity... params) throws SQLiteConstraintException {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().subjectDao().insert(params[0]);
    }

}
