package com.groupstp.cifra.entity.tasks;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum IntervalType implements EnumClass<Integer> {

    Days(10),
    Weeks(15),
    Months(20);

    private Integer id;

    IntervalType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static IntervalType fromId(Integer id) {
        for (IntervalType at : IntervalType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}