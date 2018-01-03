package com.ylimielinen.projectstudentnote.model;

import java.util.HashMap;

/**
 * Created by decai on 27.10.2017.
 * Model of the Student class
 */

public interface Student {
    //Id
    String getUid();

    //Columns
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPassword();
    HashMap<String,String> getSubjects();
}