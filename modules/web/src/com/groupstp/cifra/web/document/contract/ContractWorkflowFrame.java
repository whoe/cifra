package com.groupstp.cifra.web.document.contract;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.contract.actions.*;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This frame using for create view for each stage of workflow
 */
public class ContractWorkflowFrame extends AbstractFrame {
    // private static final Logger log = LoggerFactory.getLogger(ContractWorkflowFrame.class);

    private static final String STAGE = "stage";
    private static final String WORKFLOW = "workflow";

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private CollectionDatasource<Document, UUID> documentDs;

    @Inject
    private Table<Document> documentsTable;

    @Inject
    protected ButtonsPanel buttonsPanel;

    @WindowParam(name = STAGE)
    private Stage stage;

    @WindowParam(name = WORKFLOW)
    private Workflow workflow;

    @Inject
    WorkflowService workflowService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initSqlQuery();

        // неактуальные и согласованные
        Set<UUID> withoutButtons = new HashSet<>();
        withoutButtons.add(UUID.fromString("076d1784-3802-cd8d-705c-89c41873f4f3"));
        withoutButtons.add(UUID.fromString("95340236-b455-1600-02c3-5b1f8d8fa7c1"));
        if (withoutButtons.contains(stage.getUuid())) {
            return;
        }

        // проблемные
        if (stage.getUuid().equals(UUID.fromString("58f3b756-7eee-3ef3-7d4b-2ac44730d67a"))) {
            initEditButton();
            initRepeatContractButton();
            initRefreshButton();
            initIrrelevantButton();
            return;
        }

        initBrowseButton();

        // бухгалтер по договорам
        if (stage.getUuid().equals(UUID.fromString("420d4f71-d062-f998-8a4b-6c4a66463337")))
            initEnteredContractButton();
        else
            initCoordinateButton();

        initProblemButton();
        initHistoryButton();

    }

    private void initIrrelevantButton() {
        Button irrelevantButton = componentsFactory.createComponent(Button.class);
        Action action = new IrrelevantAction(documentsTable, workflowService);
        irrelevantButton.setAction(action);
        buttonsPanel.add(irrelevantButton);
    }

    private void initEditButton() {
        Button editButton = componentsFactory.createComponent(Button.class);
        Action action = new EditContractAction(documentsTable);
        editButton.setAction(action);
        buttonsPanel.add(editButton);
    }

    private void initRefreshButton() {
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(new RefreshAction(documentsTable)
                .withCaption(getMessage("button.refresh"))
                .withIcon(CubaIcon.REFRESH.source())
        );
        buttonsPanel.add(refreshButton);
    }

    private void initRepeatContractButton() {
        Button browseButton = componentsFactory.createComponent(Button.class);
        browseButton.setAction(new RepeatContractAction(documentsTable));
        buttonsPanel.add(browseButton);
    }

    private void initEnteredContractButton() {
        Button browseButton = componentsFactory.createComponent(Button.class);
        browseButton.setAction(new EnteredContractAction(documentsTable));
        buttonsPanel.add(browseButton);
    }

    private void initHistoryButton() {
        Button historyButton = componentsFactory.createComponent(Button.class);
        historyButton.setAction(new HistoryAction(documentsTable));
        buttonsPanel.add(historyButton);
    }

    private void initProblemButton() {
        Button problemButton = componentsFactory.createComponent(Button.class);
        problemButton.setAction(new ProblemAction(documentsTable, workflowService));
        buttonsPanel.add(problemButton);
    }


    private void initCoordinateButton() {
        Button problemButton = componentsFactory.createComponent(Button.class);
        problemButton.setAction(new CoordinateAction(documentsTable, workflowService));
        buttonsPanel.add(problemButton);
    }

    private void initBrowseButton() {
        Button browseButton = componentsFactory.createComponent(Button.class);
        browseButton.setAction(new BrowseAction(documentsTable, WindowManager.OpenType.DIALOG));
        buttonsPanel.add(browseButton);
    }

    //setup table datasource sql workflow
    private void initSqlQuery() {
        String sqlQuery = "select e from cifra$Document e ";
        sqlQuery = sqlQuery + "where e.stepName = '" + stage.getName() + "' and e.workflow.code = '" + workflow.getCode() + "'";
        documentDs.setQuery(sqlQuery);
        documentDs.refresh();
    }

}
