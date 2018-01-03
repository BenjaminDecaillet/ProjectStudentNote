package com.ylimielinen.projectstudentnote.model;

/**
 * Created by decai on 03.11.2017.
 * Model of the mark class
 */

public interface Mark {
    //Id
    String getUid();
    //Columns
    String getName();
    Double getValue();
    String getSubject();
    Double getWeighting();
}