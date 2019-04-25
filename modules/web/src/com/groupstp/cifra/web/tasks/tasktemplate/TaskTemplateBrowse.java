package com.groupstp.cifra.web.tasks.tasktemplate;

import com.groupstp.cifra.entity.tasks.TaskTemplate;
import com.groupstp.cifra.entity.tasks.TaskTypical;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Security;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.EntityCombinedScreen;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.web.gui.components.WebTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

import static com.groupstp.cifra.web.tasks.tasktypical.TaskTypicalBrowse.MULTIPLE_SELECT_ON;

public class TaskTemplateBrowse extends EntityCombinedScreen {

    private static final Logger log = LoggerFactory.getLogger(TaskTemplateBrowse.class);

    @Inject
    private Table<TaskTemplate> tableOfTemplates;

    @Inject
    private Table<TaskTypical> taskTypicalTable;

    @Inject
    private CollectionDatasource<TaskTypical, UUID> taskTypicalsDs;

    @Inject
    private DataManager dataManager;

    private TaskTemplate currentTemplate;

    @Override
    public void init(Map<String, Object> params) {

        // action on select template
        Action selectTaskAction = new BaseAction("") {
            @Override
            public void actionPerform(Component component) {
                Collection list = ((WebTable) component).getSelected();
                if (list.size() == 0) {
                    return;
                }
                TaskTemplate taskTemplate = (TaskTemplate) list.iterator().next();
                selectActiveTemplate(taskTemplate)
                ;
            }
        };
        tableOfTemplates.setItemClickAction(selectTaskAction);
        tableOfTemplates.setEnterPressAction(selectTaskAction);

        selectActiveTemplate(null);

        WindowParams.DISABLE_AUTO_REFRESH.set(params, true);

    }

    @Override
    public void ready() {
        super.ready();

    }

    /**
     * Change active workspace from left(template's table) to right(typical tasks) and back
     *
     * @param taskTemplate - selected template ( null - if no selected template)
     */
    private void selectActiveTemplate(TaskTemplate taskTemplate) {
        boolean flag = Objects.nonNull(taskTemplate);

        currentTemplate = taskTemplate;

        getComponentNN("lookupBox").setEnabled(!flag);
        getComponentNN("editBox").setEnabled(flag);

        boolean entityOpPermitted = AppBeans.get(Security.class).isEntityOpPermitted(TaskTemplate.class, EntityOp.UPDATE);
        getComponentNN("taskTypicalTable").setEnabled(entityOpPermitted);
        getComponentNN("save").setEnabled(entityOpPermitted);


        taskTypicalsDs.refresh();
        taskTypicalsDs.clear();

        if (currentTemplate != null) {
            loadTemplate();
        } else {
            tableOfTemplates.getDatasource().refresh();
        }

    }

    /**
     * Load typical tasks from current selected template to typical tasks table
     * In other words load current template
     */
    private void loadTemplate() {
        currentTemplate.getTasks().forEach(taskTypical -> taskTypicalsDs.addItem(taskTypical));

    }

    /**
     * Action on ADD button
     * Add typical task(s) to table
     */
    public void onAddButton() {
        openLookup(TaskTypical.class, items -> {
            for (Object e :
                    items) {
                taskTypicalsDs.addItem((TaskTypical) e);
            }
        }, WindowManager.OpenType.DIALOG, ParamsMap.of(MULTIPLE_SELECT_ON, ""));
    }

    /**
     * Action on DELETE button
     * Remove current typical task from table
     *
     */
    public void onDeleteButton() {
        Iterator<TaskTypical> iterator = taskTypicalTable.getSelected().iterator();
        if (iterator.hasNext()) {
            taskTypicalsDs.removeItem(iterator.next());
        }
    }

    /**
     * Action on CLEAR button
     * Clear typical tasks table
     *
     */
    public void onClearButton() {
        taskTypicalTable.getDatasource().clear();
    }

    /**
     * Save current selected typical tasks to current selected template
     * Change to left work space
     */
    public void saveTemplate() {
        List<TaskTypical> items = new ArrayList<>(taskTypicalsDs.getItems());
        currentTemplate.setTasks(items);
        dataManager.commit(currentTemplate);
        log.info("Template {} was saved", currentTemplate.getName());
        selectActiveTemplate(null);
    }

    /**
     * Clear typical tasks table
     * Change to left work space
     */
    public void cancelTemplate() {
        selectActiveTemplate(null);
    }
}