package com.ylimielinen.projectstudentnote.db;

import android.util.Log;

import com.ylimielinen.projectstudentnote.db.entity.StudentEntity;
import com.ylimielinen.projectstudentnote.db.entity.SubjectEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by decai on 03.11.2017.
 */

public class InitDatabase {

    public static void initializeDb(AppDatabase db){
        List<StudentEntity> students = new ArrayList<>();
        List<SubjectEntity> subjects = new ArrayList<>();
        generateData(students, subjects);
        insertData(db, students, subjects);
    }

    public static void generateData(List<StudentEntity> students, List<SubjectEntity> subjects){
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

        SubjectEntity subject1 = new SubjectEntity();
        SubjectEntity subject2 = new SubjectEntity();
        SubjectEntity subject3 = new SubjectEntity();
        SubjectEntity subject4 = new SubjectEntity();

        subject1.setDescription("Le super cours de powerBI du samedi matin");
        subject1.setName("PowerBI");
        subject1.setStudent(students.get(0).getEmail());
        subjects.add(subject1);

        subject2.setDescription("Le cours de sécurité informatique de GUEDIN");
        subject2.setName("SECURIT");
        subject2.setStudent(students.get(0).getEmail());
        subjects.add(subject2);

        subject3.setDescription("Ach j'ai eu un problème avec Mon MAC");
        subject3.setName("Statistiques");
        subject3.setStudent(students.get(1).getEmail());
        subjects.add(subject3);

        subject4.setDescription("Le cours de rattrapage ou je vais prendre cher");
        subject4.setName("ALGO");
        subject4.setStudent(students.get(1).getEmail());
        subjects.add(subject4);
    }

    private static void insertData(AppDatabase db,List<StudentEntity> students, List<SubjectEntity> subjects){
        db.beginTransaction();
        try {
            db.studentDao().insertAll(students);
            db.subjectDao().insertAll(subjects);

            for (StudentEntity stud:students) {
                Log.v("STUDENT INIT",stud.getEmail() +" "+ stud.getPassword() );
            }
            for (SubjectEntity s :subjects) {
                Log.v("Subject INIT",s.getName() +"\n "+ s.getDescription() +" "+ s.getStudent());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
