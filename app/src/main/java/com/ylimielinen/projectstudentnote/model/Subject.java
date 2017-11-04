package com.ylimielinen.projectstudentnote.model;

/**
 * Created by decai on 03.11.2017.
 */

public interface Subject {
    //Id
    Long getIdSubject();
    //Columns
    String getName();
    String getDescription();
    String getStudent();
}
