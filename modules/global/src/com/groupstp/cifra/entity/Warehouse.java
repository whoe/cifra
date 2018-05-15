package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "CIFRA_WAREHOUSE")
@Entity(name = "cifra$Warehouse")
public class Warehouse extends StandardEntity {
    private static final long serialVersionUID = -5082963754414635266L;

    @Column(name = "NAME", length = 50)
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}