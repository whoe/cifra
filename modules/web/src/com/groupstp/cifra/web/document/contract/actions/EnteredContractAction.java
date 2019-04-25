package com.groupstp.cifra.web.document.contract.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.vaadin.external.org.slf4j.LoggerFactory;

public class EnteredContractAction extends ItemTrackingAction {

    public EnteredContractAction(ListComponent target) {
        super(target, "contracts.entered");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.contract", "button.enteredContract"));
        target.addAction(this);
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        if (target.getSelected().size() > 1) {
            target.getFrame().showNotification("Требуется выбрать один договор");
        }
        Document document = (Document) target.getSingleSelected();
        target.getFrame().openEditor(
                "cifra$DocumentEnterNumber",
                document,
                WindowManager.OpenType.DIALOG)
                .addCloseWithCommitListener(() -> {
                    target.getDatasource().refresh();
                    // Завершаем рабочий процесс
                    WorkflowService service = AppBeans.get(WorkflowService.class);
                    try {
                        service.finishTask(service.getWorkflowInstanceTask(document));
                    } catch (WorkflowException e) {
                        LoggerFactory.getLogger(this.getClass())
                                .error("error last finish task to complete", e);
                    }
                });
    }
}
