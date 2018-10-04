package com.groupstp.cifra.web.tasks.task;

import com.groupstp.cifra.WorkflowProcessService;
import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskStatus;
import com.groupstp.cifra.web.entity.CifraUiEvent;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.*;

import static com.groupstp.cifra.web.tasks.task.TaskList.CONTROLTASK;
import static com.groupstp.cifra.web.tasks.task.TaskList.MYTASK;

/**
 * Frame inside our task table's tab
 */
public class TaskListFrame extends AbstractFrame {

    private static final Logger log = LoggerFactory.getLogger(TaskListFrame.class);

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private GroupDatasource<Task, UUID> tasksDs;
    @Inject
    private Table<Task> tasksTable;
    @Inject
    private ButtonsPanel buttonsPanel;

    @Inject
    private DataManager dataManager;

    @Inject
    private WorkflowProcessService workflowService;

    @WindowParam(name = TaskList.FRAME_PARAMETER)
    private String frameName;

    private Stage currentStage;

    @EventListener
    public void onCifraUiEvent(CifraUiEvent event) {
        if ("taskCommitted".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initJpqlQuery();
        initStyleProvider();
        initFrame();
    }

    private void initStyleProvider() {
        tasksTable.addStyleProvider((entity, property) -> (entity.getEndDate() != null && entity.getEndDate().before(AppBeans.get(TimeSource.class).currentTimestamp())) ? "overdue" :  null);
    }


    /**
     * Make jpql for frame
     */
    private void initJpqlQuery() {

        tasksDs.setQuery("select t from tasks$Task t "
                + "where t." + frameName
                + ".user.id='"
                + userSessionSource.getUserSession().getCurrentOrSubstitutedUser().getId()
                + "'");
        tasksDs.refresh();
    }


    /**
     * Initialize frame
     */
    private void initFrame() {

        if (MYTASK.equals(frameName)) {

            makeButton(CubaIcon.PLAY, new BaseAction("toWork") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    Set<Task> tasks = tasksTable.getSelected();

                    tasks.forEach(task -> {
                        task.setStatus(TaskStatus.Running);
                        commitTask(task, "Status of task {} changed to ToWork");
                    });
                }
            });

            makeInterruptButton();

            makeButton(CubaIcon.CHECK, new BaseAction("done") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    Set<Task> tasks = tasksTable.getSelected();

                    tasks.forEach(task -> {
                        task.setStatus(TaskStatus.Done);
                        commitTask(task, "Status of task {} changed to Done");
                    });
                }
            });

        } else if (CONTROLTASK.equals(frameName)) {

            makeButton(CubaIcon.CHECK, new BaseAction("checkedOk") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    Set<Task> tasks = tasksTable.getSelected();

                    tasks.forEach(task -> {
                        task.setStatus(TaskStatus.Checked);
                        commitTask(task, "Status of task {} changed to Checked Ok");

                    });
                }
            });

            makeButton(CubaIcon.CANCEL, new BaseAction("checkedFalse") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    Set<Task> tasks = tasksTable.getSelected();

                    tasks.forEach(task -> {
                        task.setStatus(TaskStatus.Running);
                        commitTask(task, "Status of task {} changed to Checked False");

                    });
                }
            });

            makeInterruptButton();

        }

    }

    /**
     * Make the button from Strategy and add it to screen
     *
     * @param cubaIcon - example CubaIcon.CHECK
     * @param action   - Pattern Strategy
     */
    private void makeButton(CubaIcon cubaIcon, BaseAction action) {
        Button button = componentsFactory.createComponent(Button.class);
        button.setAction(action);
        button.setCaption(getMessage(action.getId()));
        button.setIcon(cubaIcon.source());
        buttonsPanel.add(button);
    }

    /**
     * Make button "Interrupt"
     */
    private void makeInterruptButton() {
        makeButton(CubaIcon.CANCEL, new BaseAction("interrupt") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                Set<Task> tasks = tasksTable.getSelected();

                tasks.forEach(task -> {
                    task.setStatus(TaskStatus.Interrupted);
                    commitTask(task, "Status of task {} changed to Interrupted");
                });
            }
        });
    }

    /**
     * open document for current selected task
     */
    public void onOpenDocumentClick() {
        Document document = getCurrentDocument();
        if (document != null) {
            openEditor(document, WindowManager.OpenType.NEW_WINDOW);
        }
     }

    /**
     * @return document for current task
     */
    private Document getCurrentDocument() {
        Iterator<Task> currentTaskIterator = tasksTable.getSelected().iterator();
        return currentTaskIterator.hasNext() ? currentTaskIterator.next().getTaskableEntity() : null;
    }


    /**
     * @return Current workflow's stage. null if no stage.
     */
    private Stage getStageOfWorkflow() {
        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(getCurrentDocument(), workflowService.getActiveWorkflow());
        return CollectionUtils.isEmpty(tasks) ? null : tasks.get(tasks.size() - 1).getStep().getStage();
    }


    /**
     * Send notification about change step of Workflow
     */
    private void notifyUser() {
        List<WorkflowInstanceTask> tasks = workflowService.loadTasks(getCurrentDocument(), workflowService.getActiveWorkflow());
        String message = String.format(getMessage("hasWorkflowStepIteration") + ": %s", tasks.get(tasks.size() - 1).getStep().getStage().getName());
        showNotification(message, NotificationType.TRAY);
    }

    /**
     * Commit task
     *
     * @param task
     * @param s    message to log
     */
    private void commitTask(Task task, String s) {

        currentStage = getStageOfWorkflow();
        dataManager.commit(task);
        log.info(s, task);
        if (!currentStage.getUuid().toString().equals(getStageOfWorkflow().getUuid().toString())) {
            notifyUser();
        }
        tasksDs.refresh();
    }

}
