package com.ylimielinen.projectstudentnote.model;

/**
 * Created by decai on 27.10.2017.
 * Model of the Student class
 */

public interface Student {
    //Id
    String getEmail();
    //Columns
    String getFirstName();
    String getLastName();
    String getPassword();
}
