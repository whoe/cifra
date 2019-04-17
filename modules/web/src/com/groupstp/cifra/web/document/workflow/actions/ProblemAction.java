package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class ProblemAction extends ItemTrackingAction {
    public ProblemAction(ListComponent target) {
        super("contracts.problem");
        setIcon(CubaIcon.CANCEL.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.reject"));
        target.addAction(this);
    }
}
