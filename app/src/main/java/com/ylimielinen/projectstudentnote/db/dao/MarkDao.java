package com.ylimielinen.projectstudentnote.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;


import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;

import java.util.List;

/**
 * Created by decai on 10.11.2017.
 */
@Dao
public abstract class MarkDao {

    @Query("SELECT * FROM marks WHERE idMark = :idMark")
    public abstract LiveData<MarkEntity> getById(Long idMark);

    @Query("SELECT * FROM marks WHERE idMark = :idMark")
    public abstract MarkEntity getByIdSync(Long idMark);

    @Insert
    public abstract Long insert(MarkEntity mark);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<MarkEntity> marks);

    @Update
    public abstract void update(MarkEntity mark);

    @Delete
    public abstract void delete(MarkEntity mark);

    @Query("DELETE FROM marks")
    public abstract void deleteAll();

    @Query("SELECT * FROM marks")
    public abstract LiveData<List<MarkEntity>> getAll();

    @Query("SELECT * FROM marks WHERE student=:student")
    public abstract LiveData<List<MarkEntity>> getMarksOfStudent(String student);

    @Query("SELECT * FROM marks WHERE student=:student")
    public abstract List<MarkEntity> getMarksOfStudentSync(String student);

    @Query("SELECT * FROM marks WHERE subject=:idSubject")
    public abstract LiveData<List<MarkEntity>> getMarksOfSubject(Long idSubject);

    @Query("SELECT * FROM marks WHERE subject=:idSubject")
    public abstract List<MarkEntity> getMarksOfSubjectSync(Long idSubject);

    @Query("SELECT * FROM marks WHERE subject=:idSubject AND student=:student")
    public abstract List<MarkEntity> getMarksOfSubjectByStudentSync(Long idSubject, String student);
}
