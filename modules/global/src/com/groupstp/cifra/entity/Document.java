package com.groupstp.cifra.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.Lob;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.OneToMany;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import javax.persistence.ManyToOne;

@Listeners("cifra_DocumentListener")
@NamePattern("%s %s %s|number,date,description")
@Table(name = "CIFRA_DOCUMENT")
@Entity(name = "cifra$Document")
public class Document extends StandardEntity {
    private static final long serialVersionUID = -5504376796832237678L;

    @NotNull
    @Column(name = "DOC_STATUS", nullable = false)
    protected Integer docStatus;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WAREHOUSE_ID")
    protected Warehouse warehouse;

    @Column(name = "CELL", length = 50)
    protected String cell;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTRAGENT_ID")
    protected Contragent contragent;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "clear"})
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "COMPANY_ID")
    protected Company company;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIVISION_ID")
    protected Division division;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(name = "DOC_TYPE_ID")
    protected DocType docType;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "document")
    protected List<CheckList> checklist;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Column(name = "GOT_ORIGINAL")
    protected Boolean gotOriginal;

    @Column(name = "DESCRIPTION")
    protected String description;

    @NotNull
    @Column(name = "NUMBER_", nullable = false, length = 15)
    protected String number;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "DATE_", nullable = false)
    protected Date date;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_LOAD")
    protected Date dateLoad;

    @ManyToOne(fetch = FetchType.LAZY)
    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @JoinColumn(name = "DOC_CAUSE_ID")
    protected Document docCause;

    @Lob
    @Column(name = "PROBLEMS")
    protected String problems;

    @Temporal(TemporalType.DATE)
    @Column(name = "FIX_DUE")
    protected Date fixDue;

    @JoinTable(name = "CIFRA_JOURNAL_DOCUMENT_LINK",
        joinColumns = @JoinColumn(name = "DOCUMENT_ID"),
        inverseJoinColumns = @JoinColumn(name = "JOURNAL_ID"))
    @ManyToMany
    protected List<Journal> events;

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


    public void setDateLoad(Date dateLoad) {
        this.dateLoad = dateLoad;
    }


    public void setDocStatus(DocStatus docStatus) {
        this.docStatus = docStatus == null ? null : docStatus.getId();
    }


    public void setContragent(Contragent contragent) {
        this.contragent = contragent;
    }

    public Contragent getContragent() {
        return contragent;
    }


    public void setCompany(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Division getDivision() {
        return division;
    }


    public DocStatus getDocStatus() {
        return docStatus == null ? null : DocStatus.fromId(docStatus);
    }


    public DocType getDocType() {
        return docType;
    }

    public void setDocType(DocType docType) {
        this.docType = docType;
    }


    public void setChecklist(List<CheckList> checklist) {
        this.checklist = checklist;
    }

    public List<CheckList> getChecklist() {
        return checklist;
    }


    public void setEvents(List<Journal> events) {
        this.events = events;
    }

    public List<Journal> getEvents() {
        return events;
    }


    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }

    public void setGotOriginal(Boolean gotOriginal) {
        this.gotOriginal = gotOriginal;
    }

    public Boolean getGotOriginal() {
        return gotOriginal;
    }

    public void setProblems(String problems) {
        this.problems = problems;
    }

    public String getProblems() {
        return problems;
    }

    public void setFixDue(Date fixDue) {
        this.fixDue = fixDue;
    }

    public Date getFixDue() {
        return fixDue;
    }


    public void setDocCause(Document docCause) {
        this.docCause = docCause;
    }

    public Document getDocCause() {
        return docCause;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public Date getDateLoad() {
        return dateLoad;
    }


}