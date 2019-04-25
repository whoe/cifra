package com.groupstp.cifra.web.tasks.taskableentity;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.tasks.TaskTemplate;
import com.groupstp.cifra.entity.tasks.TaskTypical;
import com.groupstp.cifra.entity.tasks.TaskableEntity;
import com.groupstp.cifra.web.entity.CifraUiEvent;
import com.groupstp.cifra.web.tasks.UITasksUtils;
import com.groupstp.cifra.web.tasks.task.TaskEdit;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;
import java.util.*;

/**
 * Controller for component's main screen (example)
 */
@Listeners("myListener")
public class TaskableEntityFrame extends AbstractFrame {

    @Inject
    private Table<Document> entitiesTable;

    @Inject
    private CollectionDatasource<Document, UUID> entitiesDs;

    @Inject
    private ButtonsPanel buttonsPanel;

    @Inject
    private UITasksUtils uiTasksUtils;

    @EventListener
    public void onCifraUiEvent(CifraUiEvent event) {
        if ("documentCommitted".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

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

        uiTasksUtils.makeButton(CubaIcon.CALENDAR_PLUS_O, new BaseAction("assigntask") {
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
                    uiTasksUtils.createTaskInWindows(document, items.iterator(), getFrame());

                }, WindowManager.OpenType.DIALOG));
            }
        }, getMessage("assigntask"), buttonsPanel);

        uiTasksUtils.makeButton(CubaIcon.CALENDAR, new BaseAction("assigntasks") {
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

                    assignTaskOnTemplate(selectedDocument, (TaskTemplate) selectedTemplate.iterator().next(), getFrame());

                }, WindowManager.OpenType.DIALOG));
            }
        }, getMessage("assigntasks"), buttonsPanel);

    }

    /**
     * Assign task to document on template
     *
     * @param document document
     * @param template template
     */
    private void assignTaskOnTemplate(TaskableEntity document, TaskTemplate template, Frame frame) {

        if (template.getTasks().size() == 0) return;

        Iterator<TaskTypical> iteratorTemplate = template.getTasks().iterator();

        TaskEdit taskEditWindow = uiTasksUtils.createTaskInWindows(document, iteratorTemplate, frame);

        taskEditWindow.addCloseWithCommitListener(() -> showOptionDialog(
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
                }));

    }
}

