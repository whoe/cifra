package com.groupstp.cifra.web.document;

import com.groupstp.cifra.Utils;
import com.groupstp.cifra.WorkflowProcessService;
import com.groupstp.cifra.entity.*;
import com.groupstp.workflowstp.entity.StepDirection;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.data.impl.CollectionPropertyDatasourceImpl;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class DocumentEdit extends AbstractEditor<Document> {

    private static final Logger log = LoggerFactory.getLogger(DocumentEdit.class);

    public static final String EDITABLE = "editable";
    public static final String STAGE = "stage";
    public static final String WORKFLOW = "workflow";

    @Inject
    private Datasource<Document> documentDs;

    @Inject
    private CollectionPropertyDatasourceImpl<CheckList, UUID> checklistDs;

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
    private DataManager dataManager;

    @Inject
    private WorkflowProcessService workflowService;

    @Inject
    private ComponentsFactory componentsFactory;

    private boolean fileAttachedWhenFrameWasOpened;

    private boolean checkListStateWhenFrameWasOpened;

    @Override
    protected void initNewItem(Document item) {
        super.initNewItem(item);
        item.setDocStatus(DocStatus.NEW);
    }

    @Override
    public void init(Map<String, Object> params) {

        documentDs.addItemPropertyChangeListener(e -> {
            if ("docType".equals(e.getProperty())) {
                checklistDs.clear();
                checklistDs.clearCommitLists();
                List<CheckList> items = checkListService.fillCheckList(getItem());
                items.forEach(checklistDs::addItem);
            } else if ("file".equals(e.getProperty())) {
                dateLoad.setValue(new Date());
            }
        });

        ((DataGrid<CheckList>) getComponent("checkListDataGrid")).addItemClickListener(event -> {
            if ("checked".equals(event.getColumnId())) {
                CheckList item = event.getItem();
                item.setChecked(!item.getChecked());
                checklistDs.modifyItem(item);
            }
        });

        company.addLookupAction();
        company.addOpenAction();
        company.addClearAction();

        addListenerForStartingWorkflow();

    }

    /**
     * After commit document to DB start workflow processing
     */
    private void addListenerForStartingWorkflow() {

        dsContext.addAfterCommitListener((context, result) -> {

            Workflow workflow = workflowService.getActiveWorkflow();
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
        fileAttachedWhenFrameWasOpened = getItem().getFile() != null;
        checkListStateWhenFrameWasOpened = getCheckListStatus();

        refreshLabelCurrentWorkflowStage();

        makeButtonWorkflow(new BaseAction(Utils.STEP_ISSUE_NAME) {
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
                commitIfNeeded();
                Document document = getItem();
                try {
                    List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document, workflowService.getActiveWorkflow());
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

        makeButtonWorkflow(new BaseAction(Utils.STEP_ELIMINATE_NAME) {
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
                commitIfNeeded();
                Document document = getItem();
                try {
                    List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document, workflowService.getActiveWorkflow());
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
                commitIfNeeded();
                Document document = getItem();
                try {
                    List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document, workflowService.getActiveWorkflow());
                    WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().get();
                    HashMap<String, String> map = buildParametersMapWorkflow();
                    map.put("doc_flow_incoming", "true");
                    map.put("doc_issued", "false");
                    workflowService.finishTask(task, map);
                    tasks = workflowService.loadTasks(document, workflowService.getActiveWorkflow());
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
     * set label with current workflow's step name
     */
    private void refreshLabelCurrentWorkflowStage() {
        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(getItem(), workflowService.getActiveWorkflow());
        WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);
        if (task != null) {
            ((WebLabel) getComponent("labelCurrentWorkflowStage")).setValue(getMessage("workflow.currentStep") + ": " + task.getStep().getStage().getName());
        }
    }


    /**
     * commit dsContex (all data sources), if change has been in this session
     */
    private void commitIfNeeded() {
        if (documentDs.isModified()) {
            dsContext.commit();
        }
    }

    /**
     * Processing workflow for document
     * CONTAIN WORKFLOW'S BUSINESS LOGIC
     * @param workflow
     * @param document
     * @throws Exception handled up
     */
    private void workflowRunProcessing(Workflow workflow, Document document) throws Exception {

        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document, workflow);

        if(tasks==null) return;

        if (tasks.size() == 0) {
            //no workflow task for current document, start new
            workflowService.startWorkflow(document, workflow);

            if (document.getFile() != null) {
                tasks = workflowService.loadTasks(document, workflow);
                continueWorkflow(tasks);
            }
            return;
        }

        WorkflowInstanceTask lastTask = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);

        //tasks exist, but not exist open task = workflow finished
        if (lastTask == null) return;

        //document has no file, no next step
        if (document.getFile() == null) return;

        String lastTaskName = lastTask.getStep().getStage().getName();
        if (Utils.STEP_INCOMING_NAME.equals(lastTaskName) && (isFileAttachedWhenFrameWasOpened())) {
            continueWorkflow(tasks);
        }

        // step equal Обработано or Проблемы and was have change(file attached or checkListDataGrid changed), we must run workflow
        if (Utils.STEP_PROCESSING_NAME.equals(lastTaskName) || Utils.STEP_PROBLEM_NAME.equals(lastTaskName)) {
            if (isFileAttachedWhenFrameWasOpened() || isCheckListChanged()) {
                continueWorkflow(tasks);
            }
        }
    }

    /**
     * Build map with common parameters and continue workflow
     *
     * @param tasks list of all workflow's tasks
     * @throws WorkflowException
     */
    private void continueWorkflow(List<WorkflowInstanceTask> tasks) throws WorkflowException {
        HashMap<String, String> map = buildParametersMapWorkflow();
        WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);
        workflowService.finishTask(task, map);
    }

    /**
     * @return true, if file had been attached in this work's session
     */
    private boolean isFileAttachedWhenFrameWasOpened() {
        return !fileAttachedWhenFrameWasOpened && getItem().getFile() != null;
    }

    /**
     * @return trus, if checklist status(filled/) had been changed in this work's session
     */
    private boolean isCheckListChanged() {
        return checkListStateWhenFrameWasOpened != getCheckListStatus();
    }


    /**
     * Build Map with common parameters (keys) for continue workflow
     *
     * @return
     */
    private HashMap<String, String> buildParametersMapWorkflow() {
        HashMap<String, String> map = new HashMap<>();
        map.put(Utils.DOC_LOADED_FLAG, "true");
        map.put(Utils.CHECKLIST_STATUS_FLAG, getCheckListStatus() ? "true" : "false");
        return map;
    }

    /**
     * Check checkListDataGrid status (fully filled/no fully filled).
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
     * Make button(action, caption, icon) and place it to buttonsPanel
     * @param action Strategy pattern
     */
    public void makeButtonWorkflow(Action action) {

        if (isHasNextStep(action.getId())) {
            Button button = componentsFactory.createComponent(Button.class);
            button.setAction(action);
            ((ButtonsPanel) getComponent("workflowButtonsPanel")).add(button);
            this.addAction(action);
        }
    }

    /**
     * Check document's current workflow task has next step with defined name
     * @param nextStep name of next step
     * @return
     */
    private boolean isHasNextStep(String nextStep) {

        Document document = getItem();
        if (document == null) return false;

        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document, workflowService.getActiveWorkflow());
        for (WorkflowInstanceTask task : tasks) {
            if (task.getEndDate() == null) {
                task = dataManager.reload(task, "workflowInstanceTask-process");
                List<StepDirection> directions = task.getStep().getDirections();
                return directions.stream().anyMatch(sd -> sd.getTo().getStage().getName().equals(nextStep));
            }
        }
        return false;
    }

    public void onArchive(Component ignore) {
        ChooseWarehouseCell dialog = (ChooseWarehouseCell) openWindow("chooseWarehouseCell", WindowManager.OpenType.DIALOG);
        dialog.addCloseWithCommitListener(() -> {
            Document doc = getItem();
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