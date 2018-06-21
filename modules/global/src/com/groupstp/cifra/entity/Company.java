package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import javax.persistence.UniqueConstraint;

@NamePattern("%s|name")
@Table(name = "CIFRA_COMPANY", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_CIFRA_COMPANY_UNQ", columnNames = {"EXTERNAL_ID"})
})
@Entity(name = "cifra$Company")
public class Company extends StandardEntity {
    private static final long serialVersionUID = 7982388430114199972L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true, length = 150)
    protected String name;

    @Column(name = "INN", length = 13)
    protected String inn;

    @Column(name = "FULL_NAME")
    protected String fullName;

    @Column(name = "EXTERNAL_ID")
    protected String externalId;

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }


    public static Company find(String id) {
        LoadContext<Company> ctx = LoadContext.create(Company.class).setQuery(
                LoadContext.createQuery("select d from cifra$Company d where d.externalId=:id")
                        .setParameter("id", id));
        return AppBeans.get(DataManager.class).load(ctx);
    }

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