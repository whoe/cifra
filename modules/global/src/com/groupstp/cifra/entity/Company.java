package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "CIFRA_COMPANY")
@Entity(name = "cifra$Company")
public class Company extends StandardEntity {
    private static final long serialVersionUID = 7982388430114199972L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true, length = 10)
    protected String name;

    @Column(name = "INN", length = 13)
    protected String inn;

    @Column(name = "FULL_NAME")
    protected String fullName;

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getInn() {
        return inn;
    }


}