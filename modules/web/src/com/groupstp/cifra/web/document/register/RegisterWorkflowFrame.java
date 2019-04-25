package com.groupstp.cifra.web.document.register;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.workflow.DocumentWorkflowFrame;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
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
    Table<Document> documentsTable;

    @Inject
    private CollectionDatasource<Document, UUID> documentDs;

    private Logger log = LoggerFactory.getLogger(RegisterWorkflowFrame.class);

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
                documentDs.refresh();
            } catch (WorkflowException e) {
                log.error("finish task in register workflow", e);
            }
        }
    }

}
