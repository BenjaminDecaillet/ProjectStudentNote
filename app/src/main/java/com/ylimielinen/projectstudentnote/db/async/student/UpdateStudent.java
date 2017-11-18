package com.ylimielinen.projectstudentnote.db.async.student;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;

/**
 * Created by decai on 12.11.2017.
 * Update a student in the db
 * Param = student list
 */

public class UpdateStudent extends AsyncTask<StudentEntity, Void, Void> {

    private Context mContext;

    public UpdateStudent(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(StudentEntity... studentEntities) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        for (StudentEntity student : studentEntities)
            dbCreator.getDatabase().studentDao().update(student);
        return null;
    }
}
