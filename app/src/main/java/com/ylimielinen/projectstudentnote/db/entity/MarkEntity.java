package com.ylimielinen.projectstudentnote.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.ylimielinen.projectstudentnote.model.Mark;


/**
 * Created by decai on 04.11.2017.
 */

@Entity(tableName = "marks",
        foreignKeys = {
                @ForeignKey(
                        entity = StudentEntity.class,
                        parentColumns = "email",
                        childColumns = "student",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = SubjectEntity.class,
                        parentColumns = "idSubject",
                        childColumns = "subject",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = { @Index( value = {"student"}),@Index( value = {"subject"}) }
)
public class MarkEntity implements Mark{

    @PrimaryKey(autoGenerate = true)
    private Long idMark;
    private String name;
    private Double value;
    private String student;
    private Long subject;
    private Double weighting;


    public MarkEntity(Mark mark) {
        idMark = mark.getIdMark();
        name = mark.getName();
        value = mark.getValue();
        student = mark.getStudent();
        subject = mark.getSubject();
        weighting = mark.getWeighting();
    }

    public MarkEntity() {
    }

    @Override
    public Long getIdMark() { return idMark; }

    public void setIdMark(Long idMark) { this.idMark = idMark; }

    @Override
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String getStudent() { return student; }

    public void setStudent(String student) { this.student = student; }

    @Override
    public Double getValue() { return value; }

    public void setValue(Double value) { this.value = value; }

    @Override
    public Long getSubject() { return subject; }

    @Override
    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) { this.weighting = weighting; }

    public void setSubject(Long subject) { this.subject = subject; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MarkEntity)) return false;
        MarkEntity o = (MarkEntity) obj;
        return o.getIdMark().equals(this.getIdMark());
    }
}