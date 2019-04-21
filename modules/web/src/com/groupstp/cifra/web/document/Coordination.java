package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.workflow.RegisterHelperWindow;
import com.groupstp.cifra.web.document.workflow.actions.*;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.gui.components.Action;
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
    private Button createBtn;

    @Inject
    private Button editBtn;

    @Inject
    private Button toWorkBtn;

    @Inject
    Button historyBtn;

    @Inject
    GroupTable<Document> contracts;

    @Inject
    WorkflowService workflowService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setFrame("cifra$DocumentContractWorkflow.frame");
        initTabSheets(tabs, "coord");

        if (!isEmployee()) {
            tabs.getTab("myContracts").setVisible(false);
            return;
        }

        Action editAction = new EditContractAction(contracts);
        editBtn.setAction(editAction);

        Action createAction = new CreateContractAction(contracts);
        createBtn.setAction(createAction);

        IrrelevantAction irrelevantAction = new IrrelevantAction(contracts, workflowService);
        irrelevantBtn.setAction(irrelevantAction);

        Action toWorkAction = new ToWorkAction(contracts, workflowService);
        toWorkBtn.setAction(toWorkAction);

        HistoryAction historyAction = new HistoryAction(contracts);
        historyBtn.setAction(historyAction);


    }
}