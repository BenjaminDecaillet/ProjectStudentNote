package com.ylimielinen.projectstudentnote.db.async.subject;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

import java.util.List;

/**
 * Created by decai on 11.11.2017.
 */

public class GetMarks extends AsyncTask<Long, Void, List<MarkEntity>> {

    private Context mContext;

    public GetMarks(Context context) {
        mContext = context;
    }
    @Override
    protected List<MarkEntity> doInBackground(Long... longs) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().markDao().getMarksOfSubjectSynch(longs[0]);
    }
}
