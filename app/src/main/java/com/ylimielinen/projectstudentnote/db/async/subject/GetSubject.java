package com.ylimielinen.projectstudentnote.db.async.subject;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

/**
 * Created by decai on 04.11.2017.
 */

public class GetSubject extends AsyncTask<Long, Void, SubjectEntity>{

    private Context mContext;

    public GetSubject(Context context) {
        mContext = context;
    }

        @Override
        protected SubjectEntity doInBackground(Long... longs) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().subjectDao().getByIdSync(longs[0]);
    }
}
