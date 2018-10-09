package com.groupstp.cifra.service;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskStatus;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(TaskService.NAME)
public class TaskServiceBean implements TaskService {

    @Inject
    private DataManager dataManager;

    public boolean isItActiveTaskForDocument(Document document) {

        List<Task> tasks = dataManager.loadList(LoadContext.create(Task.class).setQuery(
                LoadContext.createQuery("select t from tasks$Task t where t.taskableEntity.id=:docId and (t.status=:taskStatusRunning or t.status=:taskStatusAssigned)")
                        .setParameter("docId", document.getId())
                        .setParameter("taskStatusAssigned", TaskStatus.Assigned)
                        .setParameter("taskStatusRunning", TaskStatus.Running)));

        return !tasks.isEmpty();
    }

}