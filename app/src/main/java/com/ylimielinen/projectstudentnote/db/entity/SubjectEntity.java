package com.ylimielinen.projectstudentnote.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.ylimielinen.projectstudentnote.model.Subject;

/**
 * Created by decai on 04.11.2017.
 */
@Entity(tableName = "subjects",
        foreignKeys =
        @ForeignKey(
                entity = StudentEntity.class,
                parentColumns = "email",
                childColumns = "student",
                onDelete = ForeignKey.CASCADE
        ),
        indices = { @Index( value = {"student"}),
        }
)
public class SubjectEntity implements Subject {

    @PrimaryKey(autoGenerate = true)
    private Long idSubject;
    private String name;
    private String description;
    private String student;

    public SubjectEntity (){}

    public SubjectEntity (Subject subject){
        idSubject = subject.getIdSubject();
        name = subject.getName();
        description =subject.getDescription();
        student = subject.getStudent();
    }

    @Override
    public Long getIdSubject() { return idSubject; }

    public void setIdSubject(Long idSubject) { this.idSubject = idSubject; }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    @Override
    public String getStudent() { return student; }

    public void setStudent(String student) { this.student = student; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof SubjectEntity)) return false;
        SubjectEntity o = (SubjectEntity) obj;
        return o.getIdSubject().equals(this.getIdSubject());
    }
}
