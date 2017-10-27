package com.ylimielinen.projectstudentnote.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;

import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;

import java.util.List;

/**
 * Created by decai on 27.10.2017.
 */

public interface StudentDao {

    @Query("SELECT * FROM students WHERE email = :id")
    LiveData<StudentEntity> getById(String id);

    @Query("SELECT * FROM students WHERE email = :id")
    StudentEntity getByIdSync(String id);

    @Insert
    long insert(StudentEntity client) throws SQLiteConstraintException;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<StudentEntity> clients);

    @Update
    void update(StudentEntity client);

    @Delete
    void delete(StudentEntity client);

    @Query("DELETE FROM students")
    void deleteAll();

    @Query("SELECT * FROM students")
    LiveData<List<StudentEntity>> getAll();

    @Query("SELECT * FROM students")
    List<StudentEntity> getAllSync();

}
