package com.groupstp.cifra.web.document.contract.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.events.NotificationEventBroadcaster;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class ToWorkAction extends ItemTrackingAction {

    private WorkflowService workflowService;

    private Logger log = LoggerFactory.getLogger(ToWorkAction.class);

    public ToWorkAction(ListComponent target, WorkflowService workflowService) {
        super(target, "toWork");
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.contract", "button.toWork"));
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
                    target.getSelected()
            ) {
                Document document = (Document) doc;
                if (workflowService.isProcessing(document)) {
                    String caption = messages.getMessage("com.groupstp.cifra.web.document.contract", "contractIsProcessing");
                    target.getFrame().showNotification(caption, Frame.NotificationType.HUMANIZED);
                } else {
                    workflowService.startWorkflow(document, workflow);

                    // publish NotificationGlobalEvent
                    // setting workflow for document to getWorkflowInstanceTask work properly
                    document.setWorkflow(workflowService.getWorkflow(document));
                    AppBeans.get(NotificationEventBroadcaster.class).publish(this, document);
                }
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
