package com.groupstp.cifra.web.document.workflow;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.DocumentEdit;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.StageType;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
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
 */
public class DocumentWorkflowFrame extends AbstractFrame {
    private static final Logger log = LoggerFactory.getLogger(DocumentWorkflowFrame.class);

    public static final String STAGE = "stage";
    public static final String WORKFLOW = "workflow";

    @Inject
    protected WorkflowService service;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected Scripting scripting;

    @Inject
    protected CollectionDatasource<Document, UUID> documentDs;
    @Inject
    protected Table<Document> documentsTable;
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
        sqlQuery = sqlQuery + "where e.stepName = '" + stage.getName() + "'";
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

        EditAction editAction = (EditAction) documentsTable.getAction(EditAction.ACTION_ID);
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
        documentsTable.addColumn(column);

        path = documentDs.getMetaClass().getPropertyPath("stepName");
        column = new Table.Column(path, "stepName");
        //noinspection ConstantConditions
        column.setType(path.getRangeJavaClass());
        column.setCaption(messages.getMessage(Document.class, "Document.stepName"));
        documentsTable.addColumn(column);

        //add actions and buttons
        CreateAction createAction = new CreateAction(documentsTable) {
            @Override
            public String getWindowId() {
                return "cifra$Document.edit";
            }
        };
        Button createButton = componentsFactory.createComponent(Button.class);
        createButton.setAction(createAction);

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
                params.put(DocumentEdit.EDITABLE, canEdit(documentsTable.getSingleSelected()));
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
        Button editButton = componentsFactory.createComponent(Button.class);
        editButton.setAction(editAction);

        RemoveAction removeAction = new RemoveAction(documentsTable) {
            @Override
            public boolean isPermitted() {
                if (super.isPermitted()) {
                    Set<Document> documents = documentsTable.getSelected();
                    if (!CollectionUtils.isEmpty(documents)) {
                        for (Document query : documents) {
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

        BaseAction runAction = null;
        Button runButton = null;
        if (workflow != null) {
            runAction = new BaseAction("run") {
                @Override
                public String getCaption() {
                    return getMessage("documentsWorkflowBrowse.startWorkflow");
                }

                @Override
                public String getIcon() {
                    return CubaIcon.OK.source();
                }

                @Override
                public void actionPerform(Component component) {
                    final Document documents = documentsTable.getSingleSelected();
                    if (documents != null) {
                        Action doAction = new DialogAction(DialogAction.Type.YES, Status.PRIMARY).withHandler(event -> {
                            try {
                                service.startWorkflow(documents, workflow);
                                showNotification(getMessage("documentsWorkflowBrowse.workflowStarted"), NotificationType.HUMANIZED);
                            } catch (WorkflowException e) {
                                log.error(String.format("Failed to launch workflow %s for document %s", workflow, documents), e);

                                showNotification(String.format(getMessage("documentsWorkflowBrowse.workflowFailed"),
                                        e.getMessage() == null ? getMessage("documentsWorkflowBrowse.notAvailable") : e.getMessage()),
                                        NotificationType.ERROR);
                            } finally {
                                documentDs.refresh();
                            }
                        });
                        showOptionDialog(
                                messages.getMainMessage("dialogs.Confirmation"),
                                getMessage("documentsWorkflowBrowse.startWorkflowConfirmation"),
                                MessageType.CONFIRMATION,
                                new Action[]{
                                        doAction,
                                        new DialogAction(DialogAction.Type.NO)
                                }
                        );
                    }
                }

                @Override
                public boolean isPermitted() {
                    if (super.isPermitted()) {
                        Set<Document> documents = documentsTable.getSelected();
                        if (!CollectionUtils.isEmpty(documents) && documents.size() == 1) {
                            return canRun(IterableUtils.get(documents, 0));
                        }
                    }
                    return false;
                }
            };
            runButton = componentsFactory.createComponent(Button.class);
            runButton.setAction(runAction);
        }

        RefreshAction refreshAction = new RefreshAction(documentsTable);
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);


        documentsTable.addAction(createAction);
        documentsTable.addAction(editAction);
        documentsTable.addAction(removeAction);
        if (runAction != null) {
            documentsTable.addAction(runAction);
        }
        documentsTable.addAction(refreshAction);
        buttonsPanel.add(createButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(removeButton);
        if (runButton != null) {
            buttonsPanel.add(runButton);
        }
        buttonsPanel.add(refreshButton);
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
                        throw new RuntimeException(getMessage("documentsWorkflowBrowse.errorOnScreenExtension"), e);
                    }
                }
            }
        }
    }

    //check what we can provide to user to delete workflow
    protected boolean canDelete(Document document) {
        return document != null && document.getStatus() == null;
    }

    //check what we can provide to user to edit workflow
    protected boolean canEdit(Document document) {
        return document != null && document.getStatus() == null;
    }

    //check what we can provide to user to run workflow
    protected boolean canRun(Document document) {
        return document != null && document.getStatus() == null;
    }
}
