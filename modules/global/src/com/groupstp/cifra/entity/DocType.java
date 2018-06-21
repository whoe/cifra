package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.*;

import java.security.Provider;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;

@NamePattern("%s|name")
@Table(name = "CIFRA_DOC_TYPE")
@Entity(name = "cifra$DocType")
public class DocType extends StandardEntity {
    private static final long serialVersionUID = -3105940981361990296L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    protected String name;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "docType")
    protected List<CheckListItems> checkListItems;

    public static DocType findType(String name) throws Exception {
        LoadContext<DocType> ctx = LoadContext.create(DocType.class).setQuery(
                LoadContext.createQuery("select d from cifra$DocType d where d.name=:name")
                        .setParameter("name", name));
        DocType dt = AppBeans.get(DataManager.class).load(ctx);
        if(dt==null) {
            dt = new DocType();
            dt.setName(name);
            AppBeans.get(DataManager.class).commit(dt);
        }
        return dt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCheckListItems(List<CheckListItems> checkListItems) {
        this.checkListItems = checkListItems;
    }

    public List<CheckListItems> getCheckListItems() {
        return checkListItems;
    }

}