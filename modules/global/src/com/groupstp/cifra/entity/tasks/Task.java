package com.groupstp.cifra.entity.tasks;

import com.groupstp.cifra.entity.Employee;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TASKS_TASK")
@Entity(name = "tasks$Task")
public class Task extends StandardEntity {

    private static final long serialVersionUID = 8564942625551240057L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TASK_TYPICAL_ID")
    protected TaskTypical taskTypical;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    protected Date endDate;


    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected Integer status;

    @Column(name = "CONTROL")
    protected Boolean controlNeeded;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected Employee author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERFORMER_ID")
    protected Employee performer;

    @Column(name = "TASKABLE_ENTITY_ID", nullable = false)
    protected String taskableEntityId;


    @Column(name = "TASKABLE_ENTITY_NAME", nullable = false)
    protected String taskableEntityName;

    @Lob
    @Column(name = "COMMENT_")
    protected String comment;

    public Employee getPerformer() {
        return performer;
    }

    public void setPerformer(Employee performer) {
        this.performer = performer;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public void setStatus(TaskStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public TaskStatus getStatus() {
        return status == null ? null : TaskStatus.fromId(status);
    }


    public void setTaskTypical(TaskTypical taskTypical) {
        this.taskTypical = taskTypical;
    }

    public TaskTypical getTaskTypical() {
        return taskTypical;
    }


    public void setControlNeeded(Boolean control) {
        this.controlNeeded = control;
    }

    public Boolean getControlNeeded() {
        return controlNeeded;
    }

    public void setStartDate(Date begin) {
        this.startDate = begin;
    }

    public Date getStartDate() {
        return startDate;
    }


    public void setEndDate(Date deadline) {
        this.endDate = deadline;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getTaskableEntityId() {
        return taskableEntityId;
    }

    public void setTaskableEntityId(String taskableEntityId) {
        this.taskableEntityId = taskableEntityId;
    }

    public String getTaskableEntityName() {
        return taskableEntityName;
    }

    public void setTaskableEntityName(String taskableEntityName) {
        this.taskableEntityName = taskableEntityName;
    }

}