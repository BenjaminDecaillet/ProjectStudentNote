package com.ylimielinen.projectstudentnote.db;

import android.util.Log;

import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by decai on 03.11.2017.
 */

public class InitDatabase {

    public static void initializeDb(AppDatabase db){
        List<StudentEntity> students = new ArrayList<>();

        generateData(students);
        insertData(db, students);
    }

    public static void generateData(List<StudentEntity> students){
        StudentEntity student1 = new StudentEntity();
        StudentEntity student2 = new StudentEntity();

        student1.setEmail("kevin@hevs.ch");
        student1.setFirstName("Kevin");
        student1.setLastName("Berret");
        student1.setPassword("machine");
        students.add(student1);

        student2.setEmail("ben@hevs.ch");
        student2.setFirstName("Benjamin");
        student2.setLastName("Décaillet");
        student2.setPassword("12345678");
        students.add(student2);
    }

    private static void insertData(AppDatabase db,List<StudentEntity> students){
        db.beginTransaction();
        try {
            db.studentDao().insertAll(students);

            for (StudentEntity stud:students) {
                Log.v("STUDENT INIT",stud.getEmail() +" "+ stud.getPassword() );
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
