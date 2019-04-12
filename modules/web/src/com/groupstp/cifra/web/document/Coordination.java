package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.workflow.RegisterHelperWindow;
import com.groupstp.cifra.web.document.workflow.actions.IrrelevantAction;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.TabSheet;

import javax.inject.Inject;
import java.util.Map;

public class Coordination extends RegisterHelperWindow {
    @Inject
    TabSheet tabs;

    @Inject
    private Button irrelevantBtn;

    @Inject
    GroupTable<Document> contracts;

    @Inject
    WorkflowService workflowService;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setFrame("cifra$DocumentContractWorkflow.frame");
        initTabSheets(tabs, "coord");

        IrrelevantAction action = new IrrelevantAction(contracts, workflowService);
        irrelevantBtn.setAction(action);
    }
}