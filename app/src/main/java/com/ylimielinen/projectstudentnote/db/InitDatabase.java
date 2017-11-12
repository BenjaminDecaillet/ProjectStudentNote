package com.ylimielinen.projectstudentnote.db;

import android.util.Log;

import com.ylimielinen.projectstudentnote.db.entity.MarkEntity;
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
        List<MarkEntity> marks = new ArrayList<>();
        generateData(students, subjects, marks);
        insertData(db, students, subjects, marks);
    }

    public static void generateData(List<StudentEntity> students, List<SubjectEntity> subjects, List<MarkEntity> marks){
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
        student2.setPassword("ben");
        students.add(student2);

        SubjectEntity subject1 = new SubjectEntity();
        SubjectEntity subject2 = new SubjectEntity();
        SubjectEntity subject3 = new SubjectEntity();
        SubjectEntity subject4 = new SubjectEntity();

        subject1.setIdSubject(1L);
        subject1.setDescription("Le super cours de powerBI du samedi matin");
        subject1.setName("PowerBI");
        subject1.setStudent(students.get(0).getEmail());
        subjects.add(subject1);

        subject2.setIdSubject(2L);
        subject2.setDescription("Le cours de sécurité informatique de GUEDIN");
        subject2.setName("SECURIT");
        subject2.setStudent(students.get(0).getEmail());
        subjects.add(subject2);

        subject3.setIdSubject(3L);
        subject3.setDescription("Ach j'ai eu un problème avec Mon MAC");
        subject3.setName("Statistiques");
        subject3.setStudent(students.get(1).getEmail());
        subjects.add(subject3);

        subject4.setIdSubject(4L);
        subject4.setDescription("Le cours de rattrapage ou je vais prendre cher");
        subject4.setName("ALGO");
        subject4.setStudent(students.get(1).getEmail());
        subjects.add(subject4);

        MarkEntity mark1 = new MarkEntity();
        MarkEntity mark2 = new MarkEntity();
        MarkEntity mark3 = new MarkEntity();
        MarkEntity mark4 = new MarkEntity();
        MarkEntity mark5 = new MarkEntity();
        MarkEntity mark6 = new MarkEntity();
        MarkEntity mark7 = new MarkEntity();
        MarkEntity mark8 = new MarkEntity();

        mark1.setName("Contrôle Continu 1");
        mark1.setStudent(students.get(0).getEmail());
        mark1.setSubject(subjects.get(0).getIdSubject());
        mark1.setValue(5.5);
        marks.add(mark1);

        mark2.setName("Modul Exam");
        mark2.setStudent(students.get(0).getEmail());
        mark2.setSubject(subjects.get(0).getIdSubject());
        mark2.setValue(5.0);
        marks.add(mark2);

        mark3.setName("Présentation GPO");
        mark3.setStudent(students.get(0).getEmail());
        mark3.setSubject(subjects.get(1).getIdSubject());
        mark3.setValue(5.0);
        marks.add(mark3);

        mark4.setName("Modul Exam");
        mark4.setStudent(students.get(0).getEmail());
        mark4.setSubject(subjects.get(1).getIdSubject());
        mark4.setValue(5.1);
        marks.add(mark4);

        mark5.setName("Contrôle continu");
        mark5.setStudent(students.get(1).getEmail());
        mark5.setSubject(subjects.get(2).getIdSubject());
        mark5.setValue(4.5);
        marks.add(mark5);

        mark6.setName("Modul Exam");
        mark6.setStudent(students.get(1).getEmail());
        mark6.setSubject(subjects.get(2).getIdSubject());
        mark6.setValue(5.0);
        marks.add(mark6);

        mark7.setName("Contrôle continu 1");
        mark7.setStudent(students.get(1).getEmail());
        mark7.setSubject(subjects.get(3).getIdSubject());
        mark7.setValue(5.4);
        marks.add(mark7);

        mark8.setName("Modul Exam");
        mark8.setStudent(students.get(1).getEmail());
        mark8.setSubject(subjects.get(3).getIdSubject());
        mark8.setValue(5.8);
        marks.add(mark8);

    }

    private static void insertData(AppDatabase db,List<StudentEntity> students, List<SubjectEntity> subjects, List<MarkEntity> marks){
        db.beginTransaction();
        try {
            db.studentDao().insertAll(students);
            db.subjectDao().insertAll(subjects);
            db.markDao().insertAll(marks);

            for (StudentEntity stud:students) {
                Log.v("STUDENT INIT",stud.getEmail() +" "+ stud.getPassword() );
            }

            for (SubjectEntity s :subjects) {
                Log.v("Subject INIT",s.getIdSubject() + " - " + s.getName() +" "+ s.getDescription() +" "+ s.getStudent());
            }
            for (MarkEntity m :marks) {
                Log.v("Mark INIT",m.getName() +" " + m.getValue()+" " + m.getStudent() +" "+ m.getSubject());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
