package com.groupstp.cifra.web.document.contract.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.events.NotificationEventBroadcaster;
import com.groupstp.workflowstp.entity.WorkflowInstanceComment;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ProblemAction extends ItemTrackingAction {
    private WorkflowService workflowService;

    private Logger log = LoggerFactory.getLogger(ProblemAction.class);

    public ProblemAction(ListComponent target, WorkflowService workflowService) {
        super("contracts.problem");
        setIcon(CubaIcon.CANCEL.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.contract", "button.reject"));
        target.addAction(this);
        this.workflowService = workflowService;
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        Metadata metadata = AppBeans.get(Metadata.class);
        for (Object doc :
                target.getSelected()) {
            WorkflowInstanceComment comment = metadata.create(WorkflowInstanceComment.class);
            Document document = (Document) doc;
            comment.setInstance(workflowService.getWorkflowInstance(document));
            comment.setAuthor(userSession.getUser());
            WorkflowInstanceTask wit = workflowService.getWorkflowInstanceTask(document);
            comment.setTask(wit);
            target.getFrame()
                    .openEditor(comment, WindowManager.OpenType.DIALOG)
                    .addCloseWithCommitListener(() -> {
                        try {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("problem", "true");
                            if (wit == null) {
                                throw new DevelopmentException("instance task is null");
                            }
                            params.put("from", wit.getStep().getStage().getName());
                            workflowService.finishTask(wit, params);
                            AppBeans.get(NotificationEventBroadcaster.class).publish(this, document);
                        } catch (WorkflowException e) {
                            log.error("Problem action perform", e);
                        }
                        target.getDatasource().refresh();
                    });
        }
    }
}
