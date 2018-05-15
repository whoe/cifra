package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%s|item")
@Table(name = "CIFRA_CHECK_LIST_ITEMS")
@Entity(name = "cifra$CheckListItems")
public class CheckListItems extends StandardEntity {
    private static final long serialVersionUID = 5468326671823053904L;

    @NotNull
    @Column(name = "ITEM", nullable = false)
    protected String item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOC_TYPE_ID")
    protected DocType docType;


    public void setDocType(DocType docType) {
        this.docType = docType;
    }

    public DocType getDocType() {
        return docType;
    }


    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }



}