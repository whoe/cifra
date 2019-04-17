package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class HistoryAction extends ItemTrackingAction {

    public HistoryAction(ListComponent target) {
        super(target, "contracts.history");
        setIcon(CubaIcon.HISTORY.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.history"));
        target.addAction(this);
    }

}
