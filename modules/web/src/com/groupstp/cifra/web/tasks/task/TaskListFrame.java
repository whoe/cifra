package com.groupstp.cifra.web.tasks.task;

import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskStatus;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RefreshAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
    DataManager dataManager;


    @WindowParam(name = TaskList.FRAME_PARAMETER)
    protected String frameName;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initJpqlQuery();
        initFrame();
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

        EditAction action = (EditAction) tasksTable.getAction(EditAction.ACTION_ID);
        Objects.requireNonNull(action).setAfterCommitHandler(entity -> tasksDs.refresh());

        if (MYTASK.equals(frameName)) {

            makeButton(CubaIcon.PLAY, new BaseAction("toWork") {
                @Override
                public void actionPerform(Component component) {
                    super.actionPerform(component);
                    Set<Task> tasks = tasksTable.getSelected();

                    tasks.forEach(task -> {
                        task.setStatus(TaskStatus.Running);
                        dataManager.commit(task);
                        log.info("Status of task {} changed to ToWork", task);
                        tasksTable.getDatasource().refresh();
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
                        dataManager.commit(task);
                        log.info("Status of task {} changed to Done", task);
                        tasksTable.getDatasource().refresh();
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
                        dataManager.commit(task);
                        log.info("Status of task {} changed to Checked Ok", task);
                        tasksTable.getDatasource().refresh();

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
                        dataManager.commit(task);
                        log.info("Status of task {} changed to Checked False", task);
                        tasksTable.getDatasource().refresh();

                    });
                }
            });

            makeInterruptButton();

        }

        RefreshAction refreshAction = new RefreshAction(tasksTable);
        Button refreshButton = componentsFactory.createComponent(Button.class);
        refreshButton.setAction(refreshAction);

        tasksTable.addAction(refreshAction);
        buttonsPanel.add(refreshButton);

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
                    dataManager.commit(task);
                    log.info("Status of task {} changed to Interrupted", task);
                    tasksTable.getDatasource().refresh();
                });
            }
        });
    }
}
