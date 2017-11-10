package com.ylimielinen.projectstudentnote.model;

/**
 * Created by decai on 03.11.2017.
 */

public interface Mark {
    //Id
    Long getIdMark();
    //Columns
    String getName();
    String getStudent();
    Double getValue();
    Long getSubject();
}
