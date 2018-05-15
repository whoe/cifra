package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
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