package com.groupstp.cifra.web.document.workflow.frame;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.workflow.QueryWorkflowEdit;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.StageType;
import com.groupstp.workflowstp.entity.Workflow;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This frame using for create view for each stage of workflow
 *
 * @author adiatullin
 * @see com.groupstp.cifra.web.document.workflow.QueryWorkflowBrowse
 */
public class QueryWorkflowBrowseTableFrame extends AbstractFrame {
    private static final Logger log = LoggerFactory.getLogger(QueryWorkflowBrowseTableFrame.class);

    public static final String STAGE = "stage";
    public static final String WORKFLOW = "workflow";

    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected Scripting scripting;

    @Inject
    protected CollectionDatasource<Document, UUID> documentDs;
    @Inject
    protected Table<Document> queriesTable;
    @Inject
    protected ButtonsPanel buttonsPanel;

    @WindowParam(name = STAGE)
    protected Stage stage;

    @WindowParam(name = WORKFLOW)
    protected Workflow workflow;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initSqlQuery();
        initStageTableBehaviour();
        initWorkflowExtension();
    }

    //setup table datasource sql workflow
    private void initSqlQuery() {
        String sqlQuery = "select e from cifra$Document e ";
        if (stage == null) {//this is my queries tab
//            sqlQuery = sqlQuery + "where e.initiator.id = '" + getUser().getId() + "'";
        } else {
            sqlQuery = sqlQuery + "where e.stepName = '" + stage.getName() + "'";
        }
        documentDs.setQuery(sqlQuery);
        documentDs.refresh();
    }

    //setup actions which depends on workflow stage
    private void initStageTableBehaviour() {
        if (stage == null) {
            initMyQueriesView();
        } else {
            initStageQueriesView();
        }

        EditAction editAction = (EditAction) queriesTable.getAction(EditAction.ACTION_ID);
        if (editAction != null) {//after editing refresh table
            editAction.setAfterCommitHandler(entity -> documentDs.refresh());
        }
    }

    //Table view for 'My queries' tab
    private void initMyQueriesView() {
        //add additional columns
        MetaPropertyPath path = documentDs.getMetaClass().getPropertyPath("status");
        Table.Column column = new Table.Column(path, "status");
        //noinspection ConstantConditions
        column.setType(path.getRangeJavaClass());
        column.setCaption(messages.getMessage(Document.class, "Document.status"));
        queriesTable.addColumn(column);

        path = documentDs.getMetaClass().getPropertyPath("stepName");
        column = new Table.Column(path, "stepName");
        //noinspection ConstantConditions
        column.setType(path.getRangeJavaClass());
        column.setCaption(messages.getMessage(Document.class, "Document.stepName"));
        queriesTable.addColumn(column);

        //add actions and buttons
        CreateAction createAction = new CreateAction(queriesTable) {
            @Override
            public String getWindowId() {
                return "query-workflow-edit";
            }
        };
        Button createButton = componentsFactory.createComponent(Button.class);
        createButton.setAction(createAction);

        EditAction editAction = new EditAction(queriesTable) {
            @Override
            public String getWindowId() {
                return "query-workflow-edit";
            }

            @Override
            public Map<String, Object> getWindowParams() {
                Map<String, Object> params = new HashMap<>();
                Map<String, Object> superParams = super.getWindowParams();
                if (superParams != null && superParams.size() > 0) {
                    params.putAll(superParams);
                }
                params.put(QueryWorkflowEdit.EDITABLE, canEdit(queriesTable.getSingleSelected()));
                return params;
            }

            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Document> queries = queriesTable.getSelected();
                    return !CollectionUtils.isEmpty(queries) && queries.size() == 1;
                }
                return false;
            }
        };
        Button editButton = componentsFactory.createComponent(Button.class);
        editButton.setAction(editAction);

        RemoveAction removeAction = new RemoveAction(queriesTable) {
            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Document> queries = queriesTable.getSelected();
                    if (!CollectionUtils.isEmpty(queries)) {
                        for (Document query : queries) {
                            if (!canDelete(query)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        Button removeButton = componentsFactory.createComponent(Button.class);
        removeButton.setAction(removeAction);

        RefreshAction refreshAction = new RefreshAction(queriesTable);
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);


        queriesTable.addAction(createAction);
        queriesTable.addAction(editAction);
        queriesTable.addAction(removeAction);
        queriesTable.addAction(refreshAction);
        buttonsPanel.add(createButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
        buttonsPanel.add(refreshButton);
    }

    //Base table view for non 'My Queries' tab
    private void initStageQueriesView() {
        //setup actions and buttons
        EditAction editAction = new EditAction(queriesTable) {
            @Override
            public String getWindowId() {
                return "query-workflow-edit";
            }

            @Override
            public Map<String, Object> getWindowParams() {
                Map<String, Object> params = new HashMap<>();
                Map<String, Object> superParams = super.getWindowParams();
                if (superParams != null && superParams.size() > 0) {
                    params.putAll(superParams);
                }
                params.put(QueryWorkflowEdit.EDITABLE, false);
                params.put(QueryWorkflowEdit.STAGE, stage);
                params.put(QueryWorkflowEdit.WORKFLOW, workflow);
                return params;
            }

            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Document> queries = queriesTable.getSelected();
                    return !CollectionUtils.isEmpty(queries) && queries.size() == 1;
                }
                return false;
            }
        };
        Button viewButton = componentsFactory.createComponent(Button.class);
        viewButton.setAction(editAction);

        RefreshAction refreshAction = new RefreshAction(queriesTable);
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);


        queriesTable.addAction(editAction);
        queriesTable.addAction(refreshAction);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(refreshButton);
    }

    private void initWorkflowExtension() {
        if (stage != null && workflow != null) {//this is not default tab, we must extend its view by stage behaviour
            if (StageType.USERS_INTERACTION.equals(stage.getType())) {
                final String script = stage.getBrowseScreenGroovyScript();
                if (!StringUtils.isEmpty(script)) {
                    final Map<String, Object> binding = new HashMap<>();
                    binding.put("stage", stage);
                    binding.put("workflow", workflow);
                    binding.put("screen", this);
                    try {
                        scripting.evaluateGroovy(script, binding);
                    } catch (Exception e) {
                        log.error("Failed to evaluate browse screen groovy for workflow {}({}) and stage {}({})",
                                workflow, workflow.getId(), stage, stage.getId());
                        throw new RuntimeException(getMessage("queryWorkflowBrowseTableFrame.errorOnScreenExtension"), e);
                    }
                }
            }
        }
    }

    //check what we can provide to user to delete workflow
    protected boolean canDelete(Document query) {
        return query != null && query.getStatus() == null;
    }

    //check what we can provide to user to edit workflow
    protected boolean canEdit(Document query) {
        return query != null && query.getStatus() == null;
    }

}
