package com.groupstp.cifra.web.document.register;

import com.groupstp.cifra.web.document.workflow.WorkflowHelperWindow;
import com.haulmont.cuba.gui.components.TabSheet;

import javax.inject.Inject;
import java.util.Map;

/**
 * Реестр исходящих писем
 */
public class OutgoingRegister extends WorkflowHelperWindow {

    @Inject
    TabSheet tabs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initTabSheets(tabs,
                "outreg",
                "cifra$RegisterWorkflow.frame"
        );
    }
}