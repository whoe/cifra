package com.groupstp.cifra.web.tasks.taskableentity;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskTemplate;
import com.groupstp.cifra.entity.tasks.TaskTypical;
import com.groupstp.cifra.entity.tasks.TaskableEntity;
import com.groupstp.cifra.web.tasks.task.TaskEdit;
import com.groupstp.cifra.web.tasks.task.TaskListFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

import static com.groupstp.cifra.web.tasks.Utils.countEndDateFromStartDate;

/**
 * Controller for component's main screen (example)
 */
@Listeners("myListener")
public class TaskableEntityFrame extends AbstractFrame {

    private static final Logger log = LoggerFactory.getLogger(TaskListFrame.class);

    @Inject
    private Table<Document> entitiesTable;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private CollectionDatasource<Document, UUID> entitiesDs;

    @Inject
    private ButtonsPanel buttonsPanel;

    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;

    private TaskEdit taskEditWindow;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initFrame();
    }

    /**
     * Initialize screen's components
     */
    private void initFrame() {

        EditAction action = (EditAction) entitiesTable.getAction(EditAction.ACTION_ID);
        Objects.requireNonNull(action).setAfterCommitHandler(entity -> entitiesDs.refresh());

        makeButton(CubaIcon.CALENDAR_PLUS_O, new BaseAction("assigntask") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                Set<Document> selectedDocuments = entitiesTable.getSelected();

                selectedDocuments.forEach(document -> openLookup(TaskTypical.class, items -> {
                    if (items.size() == 0) {
                        return;
                    } else if (items.size() > 1) {
                        throw new IllegalArgumentException("Only one typical task can be selected!");
                    }
                    createTaskInWindows(document, items.iterator());

                }, WindowManager.OpenType.DIALOG));
            }
        });

        makeButton(CubaIcon.CALENDAR, new BaseAction("assigntasks") {
            @Override
            public void actionPerform(Component component) {
                super.actionPerform(component);
                Set<Document> selectedDocuments = entitiesTable.getSelected();

                selectedDocuments.forEach(selectedDocument -> openLookup(TaskTemplate.class, selectedTemplate -> {
                    if (selectedTemplate.size() == 0) {
                        return;
                    } else if (selectedTemplate.size() > 1) {
                        throw new IllegalArgumentException("Only one typical task can be selected!");
                    }

                    assignTaskOnTemplate(selectedDocument, (TaskTemplate) selectedTemplate.iterator().next());

                }, WindowManager.OpenType.DIALOG));
            }
        });

    }

    /**
     * Assign task to document on template
     *
     * @param document
     * @param template
     */
    private void assignTaskOnTemplate(TaskableEntity document, TaskTemplate template) {

        if (template.getTasks().size() == 0) return;

        Iterator<TaskTypical> iteratorTemplate = template.getTasks().iterator();

        taskEditWindow = createTaskInWindows(document, iteratorTemplate);

        taskEditWindow.addCloseWithCommitListener(() -> showOptionDialog(
                getMessage("templateModeWindow"),
                getMessage("templateModeAsk"),
                MessageType.CONFIRMATION,
                new Action[]{
                        new DialogAction(DialogAction.Type.OK, Action.Status.PRIMARY).withHandler(actionPerformedEvent -> createTaskWithoutWindows(iteratorTemplate, taskEditWindow.getItem())),
                        new DialogAction(DialogAction.Type.NO, Action.Status.NORMAL).withHandler(actionPerformedEvent -> {
                            while (iteratorTemplate.hasNext()) {
                                createTaskInWindows(document, iteratorTemplate);
                            }
                        })
                }));

    }

    /**
     * Create tasks from sampleTask, copy all fields except taskTypical and endDate
     *
     * @param iteratorTemplate iterator of typical tasks from template
     * @param sampleTask       Sample task
     */
    private void createTaskWithoutWindows(Iterator<TaskTypical> iteratorTemplate, Task sampleTask) {
        while (iteratorTemplate.hasNext()) {
            TaskTypical template = iteratorTemplate.next();
            Task newTask = metadata.create(Task.class);
            newTask.setTaskTypical(template);
            newTask.setStartDate(sampleTask.getStartDate());
            newTask.setEndDate(countEndDateFromStartDate(sampleTask.getEndDate(), template.getInterval(), template.getIntervalType()));
            newTask.setStatus(sampleTask.getStatus());
            newTask.setControlNeeded(sampleTask.getControlNeeded());
            newTask.setAuthor(sampleTask.getAuthor());
            newTask.setPerformer(sampleTask.getPerformer());
            newTask.setTaskableEntity(sampleTask.getTaskableEntity());
            newTask.setComment(sampleTask.getComment());
            dataManager.commit(newTask);
        }
    }

    /**
     * Create oner task fot document and open windows form
     *
     * @param document
     * @param iteratorTemplate
     * @return
     */
    private TaskEdit createTaskInWindows(TaskableEntity document, Iterator<TaskTypical> iteratorTemplate) {
        TaskTypical taskTypical;
        taskTypical = iteratorTemplate.next();
        Task newTask = metadata.create(Task.class);

        return (TaskEdit) openEditor(newTask, WindowManager.OpenType.DIALOG, ParamsMap.of("taskTypical", taskTypical, "taskableEntity", document));

    }

    /**
     * Make the button from Strategy and add it to screen
     *
     * @param cubaIcon - example CubaIcon.CHECK.source()
     * @param action   - Pattern Strategy
     */
    private void makeButton(CubaIcon cubaIcon, BaseAction action) {
        Button button = componentsFactory.createComponent(Button.class);
        button.setAction(action);
        button.setCaption(getMessage(action.getId()));
        button.setIcon(cubaIcon.source());
        buttonsPanel.add(button);
    }

}

