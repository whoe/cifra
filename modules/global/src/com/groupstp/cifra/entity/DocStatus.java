package com.groupstp.cifra.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum DocStatus implements EnumClass<Integer> {

    NEW(10),
    ARCHIVE(20),
    CORRECTIONS_NEEDED(30),
    ISSUED(40);

    private Integer id;

    DocStatus(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static DocStatus fromId(Integer id) {
        for (DocStatus at : DocStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}