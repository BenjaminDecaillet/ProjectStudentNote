package com.ylimielinen.projectstudentnote.db.async.student;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;

/**
 * Created by decai on 03.11.2017.
 */

public class GetStudent extends AsyncTask<String, Void, StudentEntity> {

    private Context mContext;

    public GetStudent(Context context) {
        mContext = context;
    }

    @Override
    protected StudentEntity doInBackground(String... strings) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().studentDao().getByEmail(strings[0]);
    }
}
