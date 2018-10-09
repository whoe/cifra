package com.groupstp.cifra.entity.tasks;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TaskStatus implements EnumClass<Integer> {

    Assigned(5),
    Running(10),
    Done(15),
    Interrupted(20),
    Checked(25);

    private Integer id;

    TaskStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static TaskStatus fromId(Integer id) {
        for (TaskStatus at : TaskStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}