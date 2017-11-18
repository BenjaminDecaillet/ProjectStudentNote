package com.ylimielinen.projectstudentnote.db.async.subject;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

/**
 * Created by decai on 04.11.2017.
 * Create an entry of a subject in the db
 * Param = Subject
 */

public class CreateSubject extends AsyncTask<SubjectEntity, Void, Boolean> {

    private Context mContext;

    public CreateSubject(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(SubjectEntity... params) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        boolean result = true;
        try {
            dbCreator.getDatabase().subjectDao().insert(params[0]);
        }catch(SQLiteConstraintException e) {
            result = false;
        }
        return result;
    }
}