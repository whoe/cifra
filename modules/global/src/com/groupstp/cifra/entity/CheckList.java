package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Table(name = "CIFRA_CHECK_LIST")
@Entity(name = "cifra$CheckList")
public class CheckList extends StandardEntity {
    private static final long serialVersionUID = 5342957278879802524L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DOCUMENT_ID")
    protected Document document;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ITEM_ID")
    protected CheckListItems item;

    @Column(name = "CHECKED")
    protected Boolean checked;

    @Column(name = "COMMENT_")
    protected String comment;

    public CheckListItems getItem() {
        return item;
    }

    public void setItem(CheckListItems item) {
        this.item = item;
    }


    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }


    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


}