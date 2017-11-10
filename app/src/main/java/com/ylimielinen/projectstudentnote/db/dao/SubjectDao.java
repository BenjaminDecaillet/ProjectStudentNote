package com.ylimielinen.projectstudentnote.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.List;

/**
 * Created by decai on 04.11.2017.
 */
@Dao
public abstract class SubjectDao {

    @Query("SELECT * FROM subjects WHERE idSubject = :idSubject")
    public abstract LiveData<SubjectEntity> getById(Long idSubject);

    @Query("SELECT * FROM subjects WHERE idSubject = :idSubject")
    public abstract SubjectEntity getByIdSync(Long idSubject);

    @Insert
    public abstract Long insert(SubjectEntity subject);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<SubjectEntity> subjects);

    @Update
    public abstract void update(SubjectEntity subject);

    @Delete
    public abstract void delete(SubjectEntity subject);

    @Query("DELETE FROM subjects")
    public abstract void deleteAll();

    @Query("SELECT * FROM subjects")
    public abstract LiveData<List<SubjectEntity>> getAll();

    @Query("SELECT * FROM subjects WHERE student=:student")
    public abstract LiveData<List<SubjectEntity>> getSubjectsOfStudent(String student);

    @Query("SELECT * FROM subjects WHERE student=:student")
    public abstract List<SubjectEntity> getSubjectOfStudentSynch(String student);
}
