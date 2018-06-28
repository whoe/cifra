package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.List;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Set;

@NamePattern("%s|name")
@Table(name = "CIFRA_TAG")
@Entity(name = "cifra$Tag")
public class Tag extends StandardEntity {
    private static final long serialVersionUID = -155575072488713390L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    protected String name;

    @JoinTable(name = "CIFRA_DOCUMENT_TAG_LINK",
        joinColumns = @JoinColumn(name = "TAG_ID"),
        inverseJoinColumns = @JoinColumn(name = "DOCUMENT_ID"))
    @ManyToMany
    protected Set<Document> documents;

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}