package com.ylimielinen.projectstudentnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.ylimielinen.projectstudentnote.model.Mark;
import com.ylimielinen.projectstudentnote.model.Student;

/**
 * Created by decai on 04.11.2017.
 */

@Entity(tableName = "marks",
        foreignKeys =
        @ForeignKey(
                entity = StudentEntity.class,
                parentColumns = "email",
                childColumns = "student",
                onDelete = ForeignKey.CASCADE
        ),
        indices = { @Index( value = {"student"}) }
)
public class MarkEntity implements Mark{

    @PrimaryKey(autoGenerate = true)
    private Long idMark;
    private String name;
    private Double value;
    private String student;


    public MarkEntity(Mark mark) {
        idMark = mark.getIdMark();
        name = mark.getName();
        value = mark.getValue();
        student = mark.getStudent();
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
    public double getValue() { return value; }

    public void setValue(double value) { this.value = value; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof MarkEntity)) return false;
        MarkEntity o = (MarkEntity) obj;
        return o.getIdMark().equals(this.getIdMark());
    }
}
