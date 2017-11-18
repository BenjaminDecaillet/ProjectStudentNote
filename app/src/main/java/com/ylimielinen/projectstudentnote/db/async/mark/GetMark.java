package com.ylimielinen.projectstudentnote.db.async.mark;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

/**
 * Created by decai on 10.11.2017.
 * Get a Mark in the Db
 * Param = IdMark
 */

public class GetMark extends AsyncTask<Long, Void, MarkEntity> {

    private Context mContext;

    public GetMark(Context context) {
        mContext = context;
    }

    @Override
    protected MarkEntity doInBackground(Long... params) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().markDao().getByIdSync(params[0]);
    }
}