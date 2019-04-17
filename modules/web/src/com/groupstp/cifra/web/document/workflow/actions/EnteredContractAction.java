package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class EnteredContractAction extends ItemTrackingAction {
    public EnteredContractAction(ListComponent target) {
        super("contracts.entered");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.enteredContract"));
        target.addAction(this);
    }
}
