package com.groupstp.cifra.web.document.contract.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.events.NotificationEventBroadcaster;
import com.groupstp.workflowstp.entity.WorkflowInstance;
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
import java.util.Map;

public class RepeatContractAction extends ItemTrackingAction {

    private Logger log = LoggerFactory.getLogger(RepeatContractAction.class);

    public RepeatContractAction(ListComponent target) {
        super(target, "repeatAction");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.contract", "button.repeat"));
        target.addAction(this);
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        Metadata metadata = AppBeans.get(Metadata.class);
        WorkflowService workflowService = AppBeans.get(WorkflowService.class);
        for (Object e :
                target.getSelected()) {
            Document document = (Document) e;
            WorkflowInstanceComment comment = metadata.create(WorkflowInstanceComment.class);
            comment.setInstance(workflowService.getWorkflowInstance((Document) e));
            comment.setAuthor(userSession.getUser());
            WorkflowInstanceTask wit = workflowService.getWorkflowInstanceTask(document);
            comment.setTask(wit);
            if (wit == null) {
                throw new DevelopmentException("workflow instance task is null");
            }
            target.getFrame()
                    .openEditor(comment, WindowManager.OpenType.DIALOG)
                    .addCloseWithCommitListener(() -> {
                                WorkflowInstance wi = wit.getInstance();
                                // отправить в отдел, который его не согласовал (отказал)
                                String name = workflowService.getExecutionContext(wi).getParam("from");
                                Map<String, String> params = new HashMap<>();
                                params.put("stageName", name);
                                params.put("problem", "false");
                                try {
                                    workflowService.finishTask(
                                            wit,
                                            params
                                    );
                                    AppBeans.get(NotificationEventBroadcaster.class).publish(this, document);
                                } catch (WorkflowException ex) {
                                    log.error("error finish task in repeatContractAction", ex);
                                }
                                target.getDatasource().refresh();
                            }
                    );
        }
    }
}
