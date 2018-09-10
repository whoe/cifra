package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.*;
import com.groupstp.workflowstp.entity.StepDirection;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class DocumentEdit extends AbstractEditor<Document> {

    private String DOC_LOADED_FLAG = "doc_loaded";
    private String CHECKLIST_STATUS_FLAG = "checklist_status";
    private String STEP_INCOMING_NAME = "Входящие";
    private String STEP_PROBLEM_NAME = "Проблема";
    private String STEP_PROCESSING_NAME = "Обработано";
    private String STEP_ISSUE_NAME = "Выдано";
    private String STEP_ELIMINATE_NAME = "Уничтожен";

    @Inject
    private Datasource<Document> documentDs;

    @Inject
    private CollectionDatasource<CheckList, UUID> checklistDs;

    @Inject
    private CheckListService checkListService;

    @Inject
    private DocumentService documentService;

    @Named("fieldGroup.docType")
    private PickerField docType;

    @Named("fieldGroup.dateLoad")
    private DateField dateLoad;

    @Named("fieldGroup.company")
    private LookupPickerField company;

    @Named("fieldGroup.tag")
    private TokenList tags;

    @Inject
    private DsContext dsContext;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private WorkflowService workflowService;

    @Inject
    ComponentsFactory componentsFactory;

    @Inject
    ButtonsPanel buttonsPanel;

    private boolean fileAttached;

    private boolean checkListStateForWorkflow;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void initNewItem(Document item) {
        super.initNewItem(item);
        item.setDocStatus(DocStatus.NEW);
    }

    @Override
    public void init(Map<String, Object> params) {

        documentDs.addItemPropertyChangeListener(e -> {
            if ("docType".equals(e.getProperty())) {
                checkListService.clearCheckList(documentDs.getItem());
                List<CheckList> items = checkListService.fillCheckList(documentDs.getItem());
                items.forEach(checklistDs::addItem);
            } else if ("file".equals(e.getProperty())) {
                dateLoad.setValue(new Date());
            }
        });

        company.addLookupAction();
        company.addOpenAction();
        company.addClearAction();

        addListenerForStartingrWorkflow();

    }

    /**
     * After commit document to DB start workflow processing
     */
    private void addListenerForStartingrWorkflow() {

        dsContext.addAfterCommitListener((context, result) -> {

            Workflow workflow = getActiveWorkflow(Document.class);
            if (workflow == null) {
                return;
            }

            Document savedDocument = (Document) result.stream().filter(entity -> entity.getClass() == Document.class).findFirst().get();

            try {
                workflowRunProcessing(workflow, savedDocument);
            } catch (Exception e) {
                throw new RuntimeException(getMessage("workflow.Error"), e);
            }
        });
    }

    @Override
    public void ready() {
        super.ready();
        fileAttached = documentDs.getItem().getFile() != null;
        checkListStateForWorkflow = getCheckListStatus();

        makeButtonWorkflow(new BaseAction(STEP_ISSUE_NAME) {
            @Override
            public String getCaption() {
                return getMessage("workflow.issue");
            }

            @Override
            public String getIcon() {
                return CubaIcon.FORWARD.source();
            }

            @Override
            public void actionPerform(Component component) {
                Document document = documentDs.getItem();
                try {
                    List<WorkflowInstanceTask> tasks = loadTasks(document, getActiveWorkflow(Document.class));
                    WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().get();
                    HashMap<String, String> map = buildParametersMapWorkflow();
                    map.put("doc_issued", "true");
                    workflowService.finishTask(task, map);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка обработки заявки", e);
                } finally {
                    close(WINDOW_COMMIT_AND_CLOSE);
                }
            }
        });

        makeButtonWorkflow(new BaseAction(STEP_ELIMINATE_NAME) {
            @Override
            public String getCaption() {
                return getMessage("workflow.eliminate");
            }

            @Override
            public String getIcon() {
                return CubaIcon.REMOVE.source();
            }

            @Override
            public void actionPerform(Component component) {
                Document document = documentDs.getItem();
                try {
                    List<WorkflowInstanceTask> tasks = loadTasks(document, getActiveWorkflow(Document.class));
                    WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().get();
                    HashMap<String, String> map = buildParametersMapWorkflow();
                    map.put("doc_eliminated", "true");
                    workflowService.finishTask(task, map);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка обработки заявки", e);
                } finally {
                    close(WINDOW_COMMIT_AND_CLOSE);
                }
            }
        });

        makeButtonWorkflow(new BaseAction("Входящий") {
            @Override
            public String getCaption() {
                return getMessage("workflow.back");
            }

            @Override
            public String getIcon() {
                return CubaIcon.BACKWARD.source();
            }

            @Override
            public void actionPerform(Component component) {
                Document document = documentDs.getItem();
                try {
                    List<WorkflowInstanceTask> tasks = loadTasks(document, getActiveWorkflow(Document.class));
                    WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().get();
                    HashMap<String, String> map = buildParametersMapWorkflow();
                    map.put("doc_flow_incoming", "true");
                    map.put("doc_issued", "false");
                    workflowService.finishTask(task, map);
                    tasks = loadTasks(document, getActiveWorkflow(Document.class));
                    task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().get();
                    map = buildParametersMapWorkflow();
                    map.put("doc_flow_incoming", "false");
                    workflowService.finishTask(task, map);
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка обработки заявки", e);
                } finally {
                    close(WINDOW_COMMIT_AND_CLOSE);
                }
            }
        });
    }

    /**
     * Processing workflow for document
     * CONTAIN WORKFLOW'S BUSINESS LOGIC
     * @param workflow
     * @param document
     * @throws Exception handled up
     */
    private void workflowRunProcessing(Workflow workflow, Document document) throws Exception {

        List<WorkflowInstanceTask> tasks = loadTasks(document, workflow);

        if(tasks==null) return;

        if (tasks.size() == 0) {
            //no workflow task for current document, start new
            workflowService.startWorkflow(document, workflow);

            if (document.getFile() != null) {
                HashMap<String, String> map = buildParametersMapWorkflow();

                tasks = loadTasks(document, workflow);
                WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);
                workflowService.finishTask(task, map);
            }
            return;
        }

        WorkflowInstanceTask lastTask = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);

        //tasks exist, but not exist open task = workflow finished
        if (lastTask == null) return;

        //document has no file, no next step
        if (document.getFile() == null) return;

        // step equal Обработано or Проблемы and was have change(file attached or checklist changed), we must run workflow
        if (lastTask.getStep().getStage().getName().equals(STEP_PROCESSING_NAME) || lastTask.getStep().getStage().getName().equals(STEP_PROBLEM_NAME)) {
            if (fileAttachedOrCheckListChanged(document)) {
                HashMap<String, String> map = buildParametersMapWorkflow();
                WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);
                workflowService.finishTask(task, map);
            }
        }
    }

    private boolean fileAttachedOrCheckListChanged(Document document) {
        return !fileAttached && document.getFile() != null || checkListStateForWorkflow != getCheckListStatus();
    }

    private HashMap<String, String> buildParametersMapWorkflow() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DOC_LOADED_FLAG, "true");
        map.put(CHECKLIST_STATUS_FLAG, getCheckListStatus() ? "true" : "false");
        return map;
    }

    /**
     * Check checklist status (fully filled/no fully filled).
     *
     * @return true if status was changed beginning from time document opened, false - otherwise
     */
    private boolean getCheckListStatus() {

        Collection<CheckList> items = checklistDs.getItems();
        for (CheckList item : items) {
            Boolean checked = item.getChecked();
            if (checked == null || !checked) {
                return false;
            }
        }
        return true;
    }

    /**
     * Load all tasks for document for workflow
     *
     * @param document
     * @param workflow
     * @return list of tasks, if no founded return empty list
     */
    private List<WorkflowInstanceTask> loadTasks(final Document document, final Workflow workflow) {
        return dataManager.loadList(LoadContext.create(WorkflowInstanceTask.class)
                .setQuery(new LoadContext.Query("select e from wfstp$" + "WorkflowInstanceTask e " +
                        "join e.instance i " +
                        "where i.workflow.id = :workflowId and i.entityId = :entityId " +
                        " order by e.createTs desc")
                        .setParameter("workflowId", workflow.getId())
                        .setParameter("entityId", document.getId().toString())
                        .setMaxResults(1))
                .setView("workflowInstanceTask-browse"));
    }

    /**
     * Return one active workflow for Class or return null
     *
     * @param classOfDocument Class
     * @return active workflow for Class
     */
    private Workflow getActiveWorkflow(Class classOfDocument) {
        String entityName = metadata.getClassNN(classOfDocument).getName();
        List<Workflow> list = dataManager.loadList(LoadContext.create(Workflow.class)
                .setQuery(new LoadContext.Query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.entityName = :entityName order by e.createTs asc")
                        .setParameter("entityName", entityName))
                .setView("query-workflow-browse"));
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                log.warn(String.format("In system existing two active workflow for entity '%s'. The first will be used", entityName));
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * Make button(action, caption, icon) and place it to buttonsPanel
     * @param action Strategy pattern
     */
    public void makeButtonWorkflow(Action action) {

        if (isHasNextStep(action.getId())) {
            Button button = componentsFactory.createComponent(Button.class);
            button.setAction(action);
            buttonsPanel.add(button);
            this.addAction(action);
        }
    }

    /**
     * Check document's current workflow task has next step with defined name
     * @param nextStep name of next step
     * @return
     */
    private boolean isHasNextStep(String nextStep) {

        Document document = documentDs.getItem();
        if (document == null) return false;

        List<WorkflowInstanceTask> tasks = loadTasks(document, getActiveWorkflow(Document.class));
        for (WorkflowInstanceTask task : tasks) {
            if (task.getEndDate() == null) {
                task = dataManager.reload(task, "workflowInstanceTask-process");
                List<StepDirection> directions = task.getStep().getDirections();
                return directions.stream().anyMatch(sd -> sd.getTo().getStage().getName().equals(nextStep));
            }
        }
        return false;
    }

    public void onCheckcheck(Component ignore) {
        Document currentDocument = documentDs.getItem();
        documentDs.commit();
        checkListService.fillCheckList(currentDocument);
        documentDs.refresh();
    }

    public void onArchive(Component ignore) {
        ChooseWarehouseCell dialog = (ChooseWarehouseCell) openWindow("chooseWarehouseCell", WindowManager.OpenType.DIALOG);
        dialog.addCloseWithCommitListener(() -> {
            Document doc = documentDs.getItem();
            doc.setWarehouse(dialog.getWarehouse().getValue());
            doc.setCell(dialog.getCell().getValue());
            documentService.ArchiveDocument(doc);
            documentDs.refresh();
        });
    }

    public void onOkBtnClick(Component ignore) {
        Boolean gotOriginal = getItem().getGotOriginal() == null ? false : getItem().getGotOriginal();
        if (gotOriginal) {

            for (CheckList object : checklistDs.getItems()) {
                Boolean checked = object.getChecked() == null ? false : object.getChecked();
                Boolean commented = object.getComment() != null;
                if (!checked && !commented) {
                    showNotification("Введите комментарии в чек-лист");
                    return;
                }
            }
            commitAndClose();
        } else {
            commitAndClose();
        }
    }

    public void onCancelBtnClick(Component ignore) {
        this.close("close");
    }

}