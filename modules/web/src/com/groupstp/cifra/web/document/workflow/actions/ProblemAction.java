package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class ProblemAction extends BaseAction {
    public ProblemAction() {
        super("contracts.problem");
        setIcon(CubaIcon.CANCEL.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow","button.reject"));
    }
}
