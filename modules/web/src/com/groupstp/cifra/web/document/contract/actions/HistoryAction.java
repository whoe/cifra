package com.groupstp.cifra.web.document.contract.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.WorkflowInstance;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class HistoryAction extends ItemTrackingAction {

    public HistoryAction(ListComponent target) {
        super(target, "contracts.history");
        setIcon(CubaIcon.HISTORY.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.contract", "button.history"));
        target.addAction(this);
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        WorkflowService service = AppBeans.get(WorkflowService.class);
        Document document = (Document) target.getSingleSelected();
        WorkflowInstance instance = service.getWorkflowInstance(document);
        target.getFrame().openWindow("cifra$ContractHistory",
                WindowManager.OpenType.NEW_TAB,
                ParamsMap.of("instance", instance));
    }
}
