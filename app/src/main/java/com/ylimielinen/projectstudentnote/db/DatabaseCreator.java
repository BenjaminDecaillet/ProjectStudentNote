package com.ylimielinen.projectstudentnote.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ylimielinen.projectstudentnote.db.AppDatabase.DATABASE_NAME;

/**
 * Created by decai on 27.10.2017.
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

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
    /**
     * Creates or returns a previously-created database.
     * <p>
     * Although this uses an AsyncTask which currently uses a serial executor, it's thread-safe.
     */
    public void createDb(Context context) {

        Log.d("DatabaseCreator", "Creating DB from " + Thread.currentThread().getName());
        new AsyncTask<Context, Void, Void>() {

            @Override
            protected Void doInBackground(Context... params) {
                Log.d(TAG, "Starting bg job " + Thread.currentThread().getName());

                Context context = params[0].getApplicationContext();

                // Reset the database to have new data on every run.
                context.deleteDatabase(DATABASE_NAME);

                // Build the database!
//                    AppDatabase db = getDatabase();
                AppDatabase db =  Room.databaseBuilder(context.getApplicationContext(),
                                        AppDatabase.class, DATABASE_NAME).build();

                // Add some data to the database
                InitDatabase.initializeDb(db);
                Log.d(TAG, "DB was populated in thread " + Thread.currentThread().getName());

                myDb = db;
                return null;
            }

            @Override
            protected void onPostExecute(Void ignored) {

            }
        }.execute(context.getApplicationContext());

    }

}