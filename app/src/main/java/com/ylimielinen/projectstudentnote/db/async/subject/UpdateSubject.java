package com.ylimielinen.projectstudentnote.db.async.subject;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

/**
 * Created by kb on 11.11.2017.
 * Update a subject in the db
 * Param = Subject
 */

public class UpdateSubject  extends AsyncTask<SubjectEntity, Void, Void> {
    private Context context;

    public UpdateSubject(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(SubjectEntity... subjectEntities) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(context);
        for (SubjectEntity subject : subjectEntities)
            dbCreator.getDatabase().subjectDao().update(subject);
        return null;
    }
}
