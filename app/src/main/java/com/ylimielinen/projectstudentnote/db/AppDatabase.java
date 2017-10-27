package com.ylimielinen.projectstudentnote.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.ylimielinen.projectstudentnote.db.dao.StudentDao;
import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;

/**
 * Created by decai on 27.10.2017.
 */
@Database(entities = {StudentEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    static final String DATABASE_NAME = "student-database";
    public abstract StudentDao studentDao();
}
