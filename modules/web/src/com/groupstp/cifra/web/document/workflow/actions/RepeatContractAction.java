package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.icons.CubaIcon;

public class RepeatContractAction extends BaseAction {

    public RepeatContractAction() {
        super("repeatAction");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow","button.repeat"));
    }
}
