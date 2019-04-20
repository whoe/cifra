package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.workflow.RegisterHelperWindow;
import com.groupstp.cifra.web.document.workflow.actions.CreateContractAction;
import com.groupstp.cifra.web.document.workflow.actions.HistoryAction;
import com.groupstp.cifra.web.document.workflow.actions.IrrelevantAction;
import com.groupstp.cifra.web.document.workflow.actions.ToWorkAction;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;

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

        Action editAction = new ItemTrackingAction(contracts, "editAction")
                .withCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.edit"))
                .withIcon("icons/edit.png")
                .withHandler(actionPerformedEvent -> this.openEditor(
                        "cifra$DocumentContract.edit",
                        contracts.getSingleSelected(),
                        WindowManager.OpenType.DIALOG,
                        ParamsMap.of("openType", DocumentContractEdit.OpenType.edit)
                ));

        contracts.addAction(editAction);
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