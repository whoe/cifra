package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class ToWorkAction extends ItemTrackingAction {

    public ToWorkAction(ListComponent target) {
        super("toWork");
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.toWork"));
        setIcon(CubaIcon.OK.source());
        target.addAction(this);
    }
}
