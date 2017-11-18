package com.ylimielinen.projectstudentnote.model;

/**
 * Created by decai on 03.11.2017.
 * Model of the mark class
 */

public interface Mark {
    //Id
    Long getIdMark();
    //Columns
    String getName();
    String getStudent();
    Double getValue();
    Long getSubject();
    Double getWeighting();
}
