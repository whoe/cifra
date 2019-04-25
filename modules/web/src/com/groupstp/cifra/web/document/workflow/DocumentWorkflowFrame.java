package com.groupstp.cifra.web.document.workflow;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.DocumentEdit;
import com.groupstp.cifra.web.entity.CifraUiEvent;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.Workflow;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.events.UiEvent;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This frame using for create view for each stage of workflow
 */
public class DocumentWorkflowFrame extends AbstractFrame implements UiEvent {

    protected static final String STAGE = "stage";
    protected static final String WORKFLOW = "workflow";

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

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initSqlQuery();
        initStageQueriesView();
    }


    private void initSqlQuery() {
        String sqlQuery = "select e from cifra$Document e ";
        sqlQuery = sqlQuery + "where e.stepName = '" + stage.getName() + "'";
        documentDs.setQuery(sqlQuery);
        documentDs.refresh();
    }

    //Base table view for non 'My Queries' tab
    private void initStageQueriesView() {
        //setup actions and buttons
        EditAction editAction = new EditAction(documentsTable) {
            @Override
            public String getWindowId() {
                return "cifra$Document.edit";
            }

            @Override
            public Map<String, Object> getWindowParams() {
                Map<String, Object> params = new HashMap<>();
                Map<String, Object> superParams = super.getWindowParams();
                if (superParams != null && superParams.size() > 0) {
                    params.putAll(superParams);
                }
                params.put(DocumentEdit.EDITABLE, false);
                params.put(DocumentEdit.STAGE, stage);
                params.put(DocumentEdit.WORKFLOW, workflow);
                return params;
            }

            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Document> documents = documentsTable.getSelected();
                    return !CollectionUtils.isEmpty(documents) && documents.size() == 1;
                }
                return false;
            }
        };

        editAction.setAfterWindowClosedHandler(
                (window, closeActionId) -> CifraUiEvent.push("documentCommitted")
        );

        Button viewButton = componentsFactory.createComponent(Button.class);
        viewButton.setAction(editAction);

        RefreshAction refreshAction = new RefreshAction(documentsTable);
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);

        documentsTable.addAction(editAction);
        documentsTable.addAction(refreshAction);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(refreshButton);
    }
}
