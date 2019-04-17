package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class RepeatContractAction extends ItemTrackingAction {

    public RepeatContractAction(ListComponent target) {
        super(target, "repeatAction");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.repeat"));
        target.addAction(this);
    }
}
