package com.ylimielinen.projectstudentnote.entity;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.ylimielinen.projectstudentnote.model.Subject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by decai on 04.11.2017.
 * Entity of the subject with definition of the db
 */
public class SubjectEntity implements Subject {

    @NonNull
    private String uid;
    private String name;
    private String description;
    private String student;

    public SubjectEntity (){}

    public SubjectEntity (Subject subject){
        uid = subject.getUid();
        name = subject.getName();
        description =subject.getDescription();
        student = subject.getStudent();
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    @Exclude
    @Override
    public String getStudent() { return student; }

    public void setStudent(String student) { this.student = student; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof SubjectEntity)) return false;
        SubjectEntity o = (SubjectEntity) obj;
        return o.getUid().equals(this.getUid());
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("description", description);
        result.put("name", name);
        result.put("student", student);

        return result;
    }
}