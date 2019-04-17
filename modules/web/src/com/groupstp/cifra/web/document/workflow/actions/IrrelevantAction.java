package com.groupstp.cifra.web.document.workflow.actions;

import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 *
 */
public class IrrelevantAction extends ItemTrackingAction {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private WorkflowService workflowService;

    public IrrelevantAction(ListComponent target, WorkflowService service) {
        super(target, "contracts.irrelevant");
        this.workflowService = service;
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.irrelevant"));
        setIcon(CubaIcon.TRASH.source());
        target.addAction(this);
    }


    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        HashMap<String, String> params = new HashMap<>();
        params.put("irrelevant", "true");
        finishTaskWithParams(params);
    }

    private void finishTaskWithParams(HashMap<String, String> params) {
        if (this.getTarget() == null) {
            return;
        }
        try {
            for (Object entity :
                    this.getTarget().getSelected()) {
                WorkflowInstanceTask instanceTask = workflowService.getWorkflowInstanceTask((WorkflowEntity) entity);
                workflowService.finishTask(instanceTask, params);
            }

        } catch (WorkflowException e) {
            log.error("Irrelevant", e);
        }
    }
}

