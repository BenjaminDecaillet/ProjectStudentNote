package com.ylimielinen.projectstudentnote.db;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;


import static com.ylimielinen.projectstudentnote.db.AppDatabase.DATABASE_NAME;

/**
 * Created by decai on 27.10.2017.
 * Creator class for the database where we instantiate the db
 */

public class DatabaseCreator {

    public static final String TAG = "DatabaseCreator";

    private AppDatabase myDb;

    private static DatabaseCreator sInstance;

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    public synchronized static DatabaseCreator getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new DatabaseCreator();
                }
            }
        }
        return sInstance;
    }

    @Nullable
    public AppDatabase getDatabase() {
        return myDb;
    }

    /**
     * Creates or returns a previously-created database.
     */
    public void createDb(Context context) {

        Log.d("DatabaseCreator", "Creating DB from " + Thread.currentThread().getName());
        new AsyncTask<Context, Void, Void>() {

            @Override
            protected Void doInBackground(Context... params) {
                Log.d(TAG, "Starting bg job " + Thread.currentThread().getName());

                Context context = params[0].getApplicationContext();

                // Build the database!
                AppDatabase db =  Room.databaseBuilder(context.getApplicationContext(),
                                        AppDatabase.class, DATABASE_NAME).build();

                myDb = db;
                return null;
            }

            @Override
            protected void onPostExecute(Void ignored) {

            }
        }.execute(context.getApplicationContext());

    }

}