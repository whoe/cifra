package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class HistoryAction extends BaseAction {

    public HistoryAction() {
        super("contracts.history");
        setIcon(CubaIcon.HISTORY.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow","button.history"));
    }

}
