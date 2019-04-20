package com.groupstp.cifra.web.document.workflow.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.groupstp.workflowstp.web.util.MapHelper;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.apache.commons.collections4.map.SingletonMap;

import java.util.HashMap;
import java.util.Map;

public class ProblemAction extends ItemTrackingAction {
    private WorkflowService workflowService;

    Logger log = LoggerFactory.getLogger(ProblemAction.class);

    public ProblemAction(ListComponent target, WorkflowService workflowService) {
        super("contracts.problem");
        setIcon(CubaIcon.CANCEL.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.reject"));
        target.addAction(this);
        this.workflowService = workflowService;
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        for (Object doc:
                target.getSelected()) {
            Document document = (Document) doc;
            WorkflowInstanceTask wit = workflowService.getWorkflowInstanceTask(document);
            // todo don't work with params
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("problem", "true");
                workflowService.finishTask(wit, params);
                // workflowService.finishTask(wit);
            } catch (WorkflowException e) {
                log.error("Problem action perform", e);
            }
        }
    }
}
