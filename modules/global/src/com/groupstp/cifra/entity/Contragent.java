package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "CIFRA_CONTRAGENT")
@Entity(name = "cifra$Contragent")
public class Contragent extends StandardEntity {
    private static final long serialVersionUID = -1186740616286174282L;

    @Column(name = "NAME", length = 50)
    protected String name;

    @Column(name = "FULL_NAME")
    protected String fullName;

    @Column(name = "INN", length = 13)
    protected String inn;

    @Column(name = "KPP", length = 13)
    protected String kpp;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getInn() {
        return inn;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getKpp() {
        return kpp;
    }


}