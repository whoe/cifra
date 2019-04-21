package com.groupstp.cifra.web.document.workflow.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;

import java.util.UUID;

public class ToWorkAction extends ItemTrackingAction {

    private WorkflowService workflowService;

    private Logger log = LoggerFactory.getLogger(ToWorkAction.class);

    public ToWorkAction(ListComponent target, WorkflowService workflowService) {
        super(target, "toWork");
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.toWork"));
        setIcon(CubaIcon.OK.source());
        target.addAction(this);
        this.workflowService = workflowService;
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        Workflow workflow = getCoordinationWorkflow();
        try {
            for (Object doc :
                    target.getSelected()) {
                workflowService.startWorkflow((Document)doc, workflow);
            }
        } catch (WorkflowException e) {
            log.error("when start workflow", e);
        }
        target.getDatasource().refresh();
    }

    private Workflow getCoordinationWorkflow() {
        DataManager dataManager = AppBeans.get(DataManager.class);
        return dataManager.load(Workflow.class)
                .id(UUID.fromString("1f26eb81-df02-8ef5-e1e7-3a2ea5d5cb49"))
                .one();
    }
}
