package com.ylimielinen.projectstudentnote.db.async.mark;

import android.content.Context;
import android.os.AsyncTask;

import com.ylimielinen.projectstudentnote.db.DatabaseCreator;
import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

/**
 * Created by kb on 12.11.2017.
 */

public class DeleteMark extends AsyncTask<MarkEntity, Void, Void>{
    private Context context;

    public DeleteMark(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(MarkEntity... markEntities) {
        DatabaseCreator dbCreator = DatabaseCreator.getInstance(context);
        for (MarkEntity mark : markEntities)
            dbCreator.getDatabase().markDao().delete(mark);
        return null;
    }
}