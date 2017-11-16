package com.ylimielinen.projectstudentnote.db.async.student;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;

/**
 * Created by decai on 27.10.2017.
 */

public class CreateStudent extends AsyncTask<StudentEntity, Void, Long> {

    private Context mContext;

    public CreateStudent(Context context) {
        mContext = context;
    }

    @Override
    protected Long doInBackground(StudentEntity... params) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().studentDao().insert(params[0]);
    }
}
