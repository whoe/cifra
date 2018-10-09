package com.groupstp.cifra.entity.tasks;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Table(name = "TASKS_TASK_TEMPLATE")
@Entity(name = "tasks$TaskTemplate")
public class TaskTemplate extends StandardEntity {
    private static final long serialVersionUID = 5279763222898435513L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true, length = 50)
    protected String name;


    @Column(name = "DESCRIPTION")
    protected String description;

    @JoinTable(name = "TEMPLATE_TASK_TYPICAL_LINK",
        joinColumns = @JoinColumn(name = "TASK_TEMPLATE_ID"),
        inverseJoinColumns = @JoinColumn(name = "TASK_TYPICAL_ID"))
    @ManyToMany
    protected Collection<TaskTypical> tasks;

    public Collection<TaskTypical> getTasks() {
        return tasks;
    }

    public void setTasks(Collection<TaskTypical> tasks) {
        this.tasks = tasks;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}