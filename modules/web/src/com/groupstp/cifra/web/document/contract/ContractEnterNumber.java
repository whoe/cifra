package com.groupstp.cifra.web.document.contract;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.data.Datasource;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class ContractEnterNumber extends AbstractEditor<Document> {
    @Inject
    WorkflowService workflowService;

    @Inject
    private Datasource<Document> documentDs;

    private Logger log = LoggerFactory.getLogger(ContractEnterNumber.class);

    @Override
    public void ready() {
        super.ready();
        this.addCloseWithCommitListener(() -> {
            Document doc = documentDs.getItem();
            WorkflowInstanceTask workflowInstanceTask = workflowService.getWorkflowInstanceTask(doc);
            try {
                workflowService.finishTask(workflowInstanceTask);
            } catch (WorkflowException e) {
                log.error("error on document enter number", e);
            }
        });
    }

}