package com.ylimielinen.projectstudentnote.db.async.mark;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

/**
 * Created by decai on 27.10.2017.
 */

public class CreateMark extends AsyncTask<MarkEntity, Void, Long> {

    private Context mContext;

    public CreateMark(Context context) {
        mContext = context;
    }

    @Override
    protected Long doInBackground(MarkEntity... markEntities) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        return dbCreator.getDatabase().markDao().insert(markEntities[0]);
    }
}
