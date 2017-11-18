package com.ylimielinen.projectstudentnote.db.async.subject;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.List;

/**
 * Created by kb on 11.11.2017.
 * Get the subjects of the Db
 * Param = null
 */
public class GetSubjects extends AsyncTask<Long, Void, List<SubjectEntity>> {
    private Context context;

    public GetSubjects(Context context){
        this.context = context;
    }

    @Override
    protected List<SubjectEntity> doInBackground(Long... longs) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(context);
        return dbCreator.getDatabase().subjectDao().getAllSync();
    }
}