package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class EnteredContractAction extends BaseAction {
    public EnteredContractAction() {
        super("contracts.entered");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.enteredContract"));
    }
}
