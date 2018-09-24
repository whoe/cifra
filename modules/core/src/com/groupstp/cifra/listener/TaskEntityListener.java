package com.groupstp.cifra.listener;

import com.groupstp.cifra.WorkflowProcessService;
import com.groupstp.cifra.entity.tasks.Task;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;

@Component("cifra_TaskEntityListener")
public class TaskEntityListener implements AfterDeleteEntityListener<Task>, AfterInsertEntityListener<Task>, AfterUpdateEntityListener<Task> {

    private static final Logger log = LoggerFactory.getLogger(TaskEntityListener.class);

    @Inject
    private DataManager dataManager;

    @Inject
    private WorkflowProcessService workflowService;

    @Override
    public void onAfterDelete(Task entity, Connection connection) {
        try {
            connection.commit();
        } catch (SQLException ignore) {
        }
        workflowService.processWorkflowWithTask(entity.getTaskableEntity());
    }


    @Override
    public void onAfterInsert(Task entity, Connection connection) {
        try {
            connection.commit();
        } catch (SQLException ignore) {
        }
        workflowService.processWorkflowWithTask(dataManager.reload(entity, "tasks-full"));
    }


    @Override
    public void onAfterUpdate(Task entity, Connection connection) {
        try {
            connection.commit();
        } catch (SQLException ignore) {
        }
        workflowService.processWorkflowWithTask(dataManager.reload(entity, "tasks-full"));
    }

}