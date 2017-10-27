package com.ylimielinen.projectstudentnote.model;

/**
 * Created by decai on 27.10.2017.
 */

public interface Student {
    String getEmail();
    String getFirstName();
    String getLastName();
    String getPassword();
    Boolean isAdmin();
}
