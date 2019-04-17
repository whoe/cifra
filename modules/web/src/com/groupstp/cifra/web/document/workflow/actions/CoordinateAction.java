package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class CoordinateAction extends ItemTrackingAction {

    public CoordinateAction(ListComponent target) {
        super("coordinateAction");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.coordinate"));
        target.addAction(this);
    }
}
