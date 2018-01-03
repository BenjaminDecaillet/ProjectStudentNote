package com.ylimielinen.projectstudentnote.entity;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.ylimielinen.projectstudentnote.model.Student;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by decai on 27.10.2017.
 * Entity of the student with definition of the db
 */
public class StudentEntity implements Student{

    @NonNull
    private String uid;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private HashMap<String, String> subjects;

    public StudentEntity() {
    }

    public StudentEntity(Student student) {
        uid = student.getUid();
        email = student.getEmail();
        firstName = student.getFirstName();
        lastName = student.getLastName();
        password = student.getPassword();
        subjects = student.getSubjects();
    }

    @Exclude
    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Exclude
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public HashMap<String, String> getSubjects() {
        if(subjects == null)
            subjects = new HashMap<String, String>();
        return subjects;
    }

    public void setSubjects(HashMap<String, String> subjects){
        this.subjects = subjects;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof StudentEntity)) return false;
        StudentEntity o = (StudentEntity) obj;
        return o.getUid()==this.getUid();
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> res = new HashMap<>();
        res.put("email", email);
        res.put("firstName", firstName);
        res.put("lastName", lastName);
        res.put("subjects", subjects);

        return res;
    }
}