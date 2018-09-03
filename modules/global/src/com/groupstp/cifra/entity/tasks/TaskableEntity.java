package com.groupstp.cifra.entity.tasks;

/**
 * Entities used this interface can work with Tasks module
 */
public interface TaskableEntity {

    String getTaskableEntityName();

    void setTaskableEntityName(String tasks);

    //must return standart getId().toString()
    String getTaskableEntityEntityID();
}
