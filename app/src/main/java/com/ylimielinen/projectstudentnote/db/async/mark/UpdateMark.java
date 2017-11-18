package com.ylimielinen.projectstudentnote.db.async.mark;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

/**
 * Created by decai on 12.11.2017.
 * Update a Mark in the db
 * Param = Mark
 */

public class UpdateMark extends AsyncTask<MarkEntity, Void, Void> {

    private Context mContext;

    public UpdateMark(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(MarkEntity... markEntities) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(mContext);
        for (MarkEntity mark : markEntities)
            dbCreator.getDatabase().markDao().update(mark);
        return null;
    }
}
