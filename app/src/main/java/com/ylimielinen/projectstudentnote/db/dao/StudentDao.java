package com.ylimielinen.projectstudentnote.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
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
@Dao
public interface StudentDao {
//
//    @Query("SELECT * FROM students WHERE idStudent = :id")
//    StudentEntity getById(int id);

    @Query("SELECT * FROM students WHERE email = :email")
    StudentEntity getByEmail(String email);

    @Insert
    long insert(StudentEntity student) throws SQLiteConstraintException;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<StudentEntity> students);

    @Update
    void update(StudentEntity student);

    @Delete
    void delete(StudentEntity student);

    @Query("DELETE FROM students")
    void deleteAll();

    @Query("SELECT * FROM students")
    List<StudentEntity> getAll();

}
