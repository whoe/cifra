package com.groupstp.cifra.web.document.workflow;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.UUID;

/**
 * This frame using for create view for each stage of workflow
 *
 */
public class RegisterWorkflowFrame extends DocumentWorkflowFrame {
    @Inject
    ButtonsPanel buttonsPanel;

    @Inject
    WorkflowService workflowService;

    @Inject
    Table documentsTable;

    @Inject
    private CollectionDatasource<Document, UUID> documentDs;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @WindowParam(name = STAGE)
    private Stage stage;

    @WindowParam(name = WORKFLOW)
    private Workflow workflow;

    /**
     * при нажатии в работу
     */
    public void run() {
        HashMap<String, String> params = new HashMap<>();
        params.put("problem", "false");
        finishTaskWithParams(params);
    }

    /**
     * при нажатии проблема
     */
    public void problem() {
        HashMap<String, String> params = new HashMap<>();
        params.put("problem", "true");
        finishTaskWithParams(params);
    }

    private void finishTaskWithParams(HashMap<String, String> params) {
        for (Object o:
            documentsTable.getSelected()) {
            WorkflowEntity entity = (WorkflowEntity) o;
            try {
                WorkflowInstanceTask instanceTask = workflowService.getWorkflowInstanceTask(entity);
                workflowService.finishTask(instanceTask, params);
            } catch (WorkflowException e) {
                log.error("finish task in register workflow", e);
            }
        }
    }

    @Override
    protected void initSqlQuery() {
        String sqlQuery = "select e from cifra$Document e ";
        sqlQuery = sqlQuery + "where e.stepName = '" + stage.getName() + "' and e.workflow.code = '"+ workflow.getCode() + "'";
        documentDs.setQuery(sqlQuery);
        documentDs.refresh();
    }
}
