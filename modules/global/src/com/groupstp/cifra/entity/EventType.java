package com.groupstp.cifra.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum EventType implements EnumClass<Integer> {

    NEW(10),
    ORIGINAL(20),
    CORRECTIONS(30),
    ARCHIVE(40),
    ISSUE(50);

    private Integer id;

    EventType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static EventType fromId(Integer id) {
        for (EventType at : EventType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}