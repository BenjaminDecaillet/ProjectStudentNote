package com.ylimielinen.projectstudentnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.ylimielinen.projectstudentnote.model.Student;

/**
 * Created by decai on 27.10.2017.
 * Entity of the student with definition of the db
 */
@Entity(tableName = "students", primaryKeys = {"email"})
public class StudentEntity implements Student{

    @NonNull
    private String email;

    @ColumnInfo(name = "firstname")
    private String firstName;

    @ColumnInfo(name = "lastname")
    private String lastName;

    private String password;


    public StudentEntity() {
    }

    public StudentEntity(Student student) {
        email = student.getEmail();
        firstName = student.getFirstName();
        lastName = student.getLastName();
        password = student.getPassword();
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

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof StudentEntity)) return false;
        StudentEntity o = (StudentEntity) obj;
        return o.getEmail()==this.getEmail();
    }

}
