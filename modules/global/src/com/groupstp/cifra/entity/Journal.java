package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

@NamePattern("%s %s %s|eventType,doc,createTs")
@Table(name = "CIFRA_JOURNAL")
@Entity(name = "cifra$Journal")
public class Journal extends StandardEntity {
    private static final long serialVersionUID = 37278130494467877L;

    @JoinTable(name = "CIFRA_JOURNAL_DOCUMENT_LINK",
        joinColumns = @JoinColumn(name = "JOURNAL_ID"),
        inverseJoinColumns = @JoinColumn(name = "DOCUMENT_ID"))
    @ManyToMany
    protected List<Document> doc;

    @Column(name = "PREVIOUS_STATUS")
    protected Integer previousStatus;

    @NotNull
    @Column(name = "EVENT_TYPE", nullable = false)
    protected Integer eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSIBLE_ID")
    protected Employee responsible;

    @Column(name = "CELL")
    protected String cell;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID")
    protected Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HOLDER_ID")
    protected Employee holder;

    public void addDocument(Document document){
        if(this.doc==null) this.doc=new ArrayList<Document>();
        this.doc.add(document);
    }

    public void setPreviousStatus(DocStatus previousStatus) {
        this.previousStatus = previousStatus == null ? null : previousStatus.getId();
    }

    public DocStatus getPreviousStatus() {
        return previousStatus == null ? null : DocStatus.fromId(previousStatus);
    }


    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getCell() {
        return cell;
    }


    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }


    public void setDoc(List<Document> doc) {
        this.doc = doc;
    }

    public List<Document> getDoc() {
        return doc;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType == null ? null : eventType.getId();
    }

    public EventType getEventType() {
        return eventType == null ? null : EventType.fromId(eventType);
    }

    public void setResponsible(Employee responsible) {
        this.responsible = responsible;
    }

    public Employee getResponsible() {
        return responsible;
    }

    public void setHolder(Employee holder) {
        this.holder = holder;
    }

    public Employee getHolder() {
        return holder;
    }


}