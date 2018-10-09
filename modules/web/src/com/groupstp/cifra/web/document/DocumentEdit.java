package com.groupstp.cifra.web.document;

import com.groupstp.cifra.Utils;
import com.groupstp.cifra.WorkflowProcessService;
import com.groupstp.cifra.entity.CheckList;
import com.groupstp.cifra.entity.CheckListService;
import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskTemplate;
import com.groupstp.cifra.entity.tasks.TaskTypical;
import com.groupstp.cifra.entity.tasks.TaskableEntity;
import com.groupstp.cifra.service.TaskService;
import com.groupstp.cifra.web.entity.CifraUiEvent;
import com.groupstp.cifra.web.tasks.UITasksUtils;
import com.groupstp.cifra.web.tasks.task.TaskEdit;
import com.groupstp.workflowstp.entity.StepDirection;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.data.impl.CollectionDatasourceImpl;
import com.haulmont.cuba.gui.data.impl.CollectionPropertyDatasourceImpl;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.web.gui.components.WebLabel;
import com.haulmont.cuba.web.theme.HaloTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static com.groupstp.cifra.web.UIUtils.*;

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
    private CollectionDatasourceImpl<Task, UUID> tasksDs;

    @Inject
    private CheckListService checkListService;

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

    @Inject
    private ButtonsPanel tasksButtonsPanel;

    @Inject
    private Security security;

    private Document document;

    private boolean fileAttachedWhenFrameWasOpened;

    private boolean checkListStateWhenFrameWasOpened;

    private final UITasksUtils uiTasksUtils = UITasksUtils.INSTANCE;

    @Override
    protected void initNewItem(Document item) {
        super.initNewItem(item);
    }

    @Override
    public void init(Map<String, Object> params) {
        addListenerForComponents();

        company.addLookupAction();
        company.addOpenAction();
        company.addClearAction();

        addListenerForStartingWorkflow();
    }

    /**
     * add Listener to components
     */
    private void addListenerForComponents() {
        documentDs.addItemPropertyChangeListener(e -> {
            if ("docType".equals(e.getProperty())) {
                checklistDs.clear();
                checklistDs.clearCommitLists();
                List<CheckList> items = checkListService.fillCheckList(document);
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

        ((Table<Task>) getComponent("tasksListDataGrid")).setItemClickAction(new BaseAction("taskOpen") {
            @Override
            public void actionPerform(Component component) {
                Task selectedTask = ((Table<Task>) getComponent("tasksListDataGrid")).getSelected().iterator().next();
                AbstractEditor abstractEditor = openEditor(selectedTask, WindowManager.OpenType.DIALOG);
                abstractEditor.addCloseWithCommitListener(() -> tasksDs.refresh());
            }
        });
    }

    /**
     * After commit document to DB run workflow processing
     */
    private void addListenerForStartingWorkflow() {

        dsContext.addAfterCommitListener((context, result) -> {
            dsContext.refresh();

            if (workflowService.getActiveWorkflow() == null) {
                return;
            }

            document = (Document) result.stream().filter(entity -> entity.getClass() == Document.class).findFirst().get();

            workflowRunProcessing();
            CifraUiEvent.push("documentCommitted");

        });
    }

    @Override
    public void ready() {
        super.ready();
        document = getItem();
        fileAttachedWhenFrameWasOpened = document.getFile() != null;
        checkListStateWhenFrameWasOpened = getCheckListStatus();

        checkAndInitializeEmptyFields();

        refreshLabels();
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
                continueWorkflow(ParamsMap.of("doc_issued", "true"));
                close(WINDOW_COMMIT_AND_CLOSE);
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
                continueWorkflow(ParamsMap.of("doc_eliminated", "true"));
                close(WINDOW_COMMIT_AND_CLOSE);
            }
        });
        makeButtonWorkflow(new BaseAction(Utils.STEP_INCOMING_NAME) {
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
                continueWorkflow(ParamsMap.of("doc_flow_incoming", "true", "doc_issued", "false", "doc_archived", "false"));
                continueWorkflow(ParamsMap.of("doc_flow_incoming", "false"));
                close(WINDOW_COMMIT_AND_CLOSE);
            }
        });
        makeButtonWorkflow(new BaseAction(Utils.STEP_ARCHIVE_NAME) {
            @Override
            public String getCaption() {
                return getMessage("workflow.archive");
            }

            @Override
            public String getIcon() {
                return CubaIcon.ARCHIVE.source();
            }

            @Override
            public void actionPerform(Component component) {
                if (!isConditionForArchive()) {
                    showNotification(getMessage("workflow.noConditionForArchive"), NotificationType.ERROR);
                    return;
                }
                continueWorkflow(ParamsMap.of("doc_archived", true));
                close(WINDOW_COMMIT_AND_CLOSE);
            }
        });

        if (!AppBeans.get(EntityStates.class).isNew(document)) {
            initTaskButton();
        }

        checkAndSetAccessForComponents();
    }

    /**
     * check and set access for components
     * run order is important
     */
    private void checkAndSetAccessForComponents() {

        if (!security.isEntityOpPermitted(Document.class, EntityOp.UPDATE)) {
            List<Component> componentsToDisable = Arrays.asList(
                    getComponent("fieldGroup.tag"),
                    getComponent("checkListDataGrid"),
                    getComponent("workflowButtonsPanel"));
            disableComponents(componentsToDisable);
            return;
        }

        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document);
        if (tasks.isEmpty()) return;

        String nameOfLastStep = tasks.get(tasks.size() - 1).getStep().getStage().getName();
        if (Utils.STEP_ELIMINATE_NAME.equals(nameOfLastStep)) {
            editableOffComponent(new ArrayList<>(getComponents()));
            List<Component> componentsToDisable = Arrays.asList(
                    getComponent("fieldGroup.tag"),
                    getComponent("checkListDataGrid"),
                    getComponent("workflowButtonsPanel"),
                    getComponent("tasksButtonsPanel"));
            disableComponents(componentsToDisable);
        } else if (Utils.STEP_ARCHIVE_NAME.equals(nameOfLastStep)) {
            editableOffComponent(new ArrayList<>(getComponents()));
            List<Component> componentsToDisable = Arrays.asList(
                    getComponent("fieldGroup.tag"),
                    getComponent("checkListDataGrid"),
                    getComponent("workflowButtonsPanel"),
                    getComponent("tasksButtonsPanel"));
            disableComponents(componentsToDisable);
            List<Component> componentsToEnable = Arrays.asList(getComponent(Utils.STEP_INCOMING_NAME));
            enableComponents(componentsToEnable);
        }
    }

    /**
     * Send notification about change step of Workflow
     */
    private void notifyUser() {
        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document);
        String message = String.format(getMessage("hasWorkflowStepIteration") + ": %s", tasks.get(tasks.size() - 1).getStep().getStage().getName());
        showNotification(message, NotificationType.TRAY);
        CifraUiEvent.push("documentCommitted");
    }

    /**
     * fill empty checklist after document was open first time
     * start workflow after document was open first time
     */
    private void checkAndInitializeEmptyFields() {

        EntityStates entityStates = AppBeans.get(EntityStates.class);
        if (entityStates.isNew(document)) {
            return;
        }

        if (workflowService.loadTasks(document).size() == 0) {
            workflowRunProcessing();
            notifyUser();
            documentDs.refresh();
        }

        if (checklistDs.getItems().size() == 0) {
            List<CheckList> items = checkListService.fillCheckList(document);
            items.forEach(checklistDs::addItem);
        }

    }

    /**
     * refresh all labels in screen
     */
    private void refreshLabels() {
        refreshLabelCurrentWorkflowStage();
        refreshLabelTask();
    }
    /**
     * set label with current workflow's step name
     */
    private void refreshLabelCurrentWorkflowStage() {
        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document);
        WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);
        if (task != null) {
            ((WebLabel) getComponent("labelCurrentWorkflowStage")).setValue(getMessage("workflow.currentStep") + ": " + task.getStep().getStage().getName());
        }
    }

    /**
     * set label for task (be or not)
     */
    private void refreshLabelTask() {
        TaskService taskService = AppBeans.get(TaskService.class);
        ((WebLabel) getComponent("labelTasks")).setValue(getMessage(taskService.isItActiveTaskForDocument(document)? "tasks.HasActive":"tasks.NoActive"));
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
     */
    private void workflowRunProcessing() {

        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document);

        if(tasks==null) return;

            if (tasks.size() == 0) {
                //no workflow task for current document, start new
                try {
                    workflowService.startWorkflow(document, workflowService.getActiveWorkflow());
                } catch (WorkflowException ex) {
                    throw new RuntimeException(getMessage("workflow.Error"), ex);
                }
                if (document.getFile() != null) {
                    continueWorkflow();
                } else {
                    notifyUser();
                }
                return;
            }

            WorkflowInstanceTask lastTask = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);

            //tasks exist, but not exist open task = workflow finished
            if (lastTask == null) return;

            //document has no file, no next step
            if (document.getFile() == null) return;

            String lastTaskName = lastTask.getStep().getStage().getName();
            if (Utils.STEP_INCOMING_NAME.equals(lastTaskName) && (isFileAttachedChanged())) {
                continueWorkflow();
            }

            // step equal Обработано or Проблемы and was have change(file attached or checkListDataGrid changed), we must run workflow
            if (Utils.STEP_PROCESSING_NAME.equals(lastTaskName) || Utils.STEP_PROBLEM_NAME.equals(lastTaskName)) {
                if (isFileAttachedChanged() || isCheckListChanged()) {
                    continueWorkflow();
                }
            }
    }

    /**
     * Check condition
     *
     * @return
     */
    private boolean isConditionForArchive() {
        return document.getWarehouse() != null && !document.getCell().isEmpty();
    }

    /**
     * Build map with common parameters and continue workflow
     *
     * @throws WorkflowException
     */
    private void continueWorkflow() {
        continueWorkflow(null);
    }

    /**
     * Build map with common and input parameters, after that continue workflow
     *
     * @param paramsMap map of input parameters for workflow step conditions
     * @throws WorkflowException
     */
    private void continueWorkflow(Map<String, Object> paramsMap) {
        commitIfNeeded();
        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document);
        HashMap<String, String> map = buildParametersMapWorkflow();
        if (paramsMap != null) {
            paramsMap.forEach((parameter, value) -> map.putIfAbsent(parameter, value.toString()));
        }
        WorkflowInstanceTask task = tasks.stream().filter(t -> t.getEndDate() == null).findFirst().orElse(null);
        try {
            workflowService.finishTask(task, map);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка обработки заявки", e);
        }
        notifyUser();
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
     * @return true, if file had been attached in this work's session
     */
    private boolean isFileAttachedChanged() {
        return !fileAttachedWhenFrameWasOpened && document.getFile() != null;
    }

    /**
     * @return trus, if checklist status(filled/) had been changed in this work's session
     */
    private boolean isCheckListChanged() {
        return checkListStateWhenFrameWasOpened != getCheckListStatus();
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
            button.setId(action.getId());
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

        if (document == null) return false;

        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(document);
        for (WorkflowInstanceTask task : tasks) {
            if (task.getEndDate() == null) {
                task = dataManager.reload(task, "workflowInstanceTask-process");
                List<StepDirection> directions = task.getStep().getDirections();
                return directions.stream().anyMatch(sd -> sd.getTo().getStage().getName().equals(nextStep));
            }
        }
        return false;
    }

    /**
     * Action on Ok button (commit and close screen)
     *
     * @param ignore
     */
    public void onOkBtnClick(Component ignore) {
        if (checkCheckListFilledOrHasCommentary()) {
            commitAndClose();
        }
        CifraUiEvent.push("documentCommitted");
    }

    /**
     * contain business logic. Checklist's all fields must be checked up or has commentary
     *
     * @return true, if conditions are satisfied, false - otherwise
     */
    private boolean checkCheckListFilledOrHasCommentary() {
        Boolean gotOriginal = document.getGotOriginal() == null ? false : document.getGotOriginal();
        if (gotOriginal) {
            for (CheckList object : checklistDs.getItems()) {
                Boolean checked = object.getChecked() == null ? false : object.getChecked();
                Boolean commented = object.getComment() != null;
                if (!checked && !commented) {
                    showNotification("Введите комментарии в чек-лист");
                    return false;
                }
            }
        }
        return true;
    }

    public void onCancelBtnClick(Component ignore) {
        CifraUiEvent.push("documentCommitted");
        this.close("close");
    }


    /**
     * set on screen buttons for task's control
     */
    private void initTaskButton() {

        if (!security.isEntityOpPermitted(Task.class, EntityOp.CREATE)) return;

        uiTasksUtils.makeButton(CubaIcon.CALENDAR_PLUS_O, new BaseAction("assigntask") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                openLookup(TaskTypical.class, items -> {
                    if (items.size() == 0) {
                        return;
                    } else if (items.size() > 1) {
                        throw new IllegalArgumentException("Only one typical task can be selected!");
                    }
                    TaskEdit taskInWindows = uiTasksUtils.createTaskInWindows(document, items.iterator(), getFrame());
                    taskInWindows.addCloseWithCommitListener(() -> {
                        refreshLabels();
                        tasksDs.refresh();
                    });
                }, WindowManager.OpenType.DIALOG);
            }
        }, getMessage("assigntask"), tasksButtonsPanel);

        uiTasksUtils.makeButton(CubaIcon.CALENDAR, new BaseAction("assigntasks") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);

                openLookup(TaskTemplate.class, selectedTemplate -> {
                    if (selectedTemplate.size() == 0) {
                        return;
                    } else if (selectedTemplate.size() > 1) {
                        throw new IllegalArgumentException("Only one typical task can be selected!");
                    }

                    assignTaskOnTemplate(document, (TaskTemplate) selectedTemplate.iterator().next(), getFrame());
                }, WindowManager.OpenType.DIALOG);
            }
        }, getMessage("assigntasks"), tasksButtonsPanel);

    }

    /**
     * Assign task to document on template
     *
     * @param document
     * @param template
     */
    public void assignTaskOnTemplate(TaskableEntity document, TaskTemplate template, Frame frame) {

        if (template.getTasks().size() == 0) return;

        Iterator<TaskTypical> iteratorTemplate = template.getTasks().iterator();

        TaskEdit taskEditWindow = uiTasksUtils.createTaskInWindows(document, iteratorTemplate, frame);

        taskEditWindow.addCloseWithCommitListener(() -> {
            showOptionDialog(
                    getMessage("templateModeWindow"),
                    getMessage("templateModeAsk"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY).withHandler(actionPerformedEvent -> uiTasksUtils.createTaskWithoutWindows(iteratorTemplate, taskEditWindow.getItem())),
                            new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL).withHandler(actionPerformedEvent -> {
                                while (iteratorTemplate.hasNext()) {
                                    uiTasksUtils.createTaskInWindows(document, iteratorTemplate, getFrame());
                                }
                            })
                    });

            refreshLabelTask();
            tasksDs.refresh();
        });

    }

}
