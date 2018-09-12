package com.groupstp.cifra.web.tasks.task;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.TabSheet;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import javax.inject.Inject;
import java.util.Map;

/**
 * Controller for component's main screen
 * Work with tasks
 */
public class TaskList extends AbstractLookup {

    private static final String TASKABLE_ENTITY_FRAME = "tasks$TaskableEntity.browse.frame";
    private static final String TASKABLE_ENTITY_NAME = "taskableEntity";

    private static final String TASK_LIST_FRAME = "tasks$Task.browse.frame";
    public static final String MYTASK = "performer";
    public static final String CONTROLTASK = "author";
    public static final String FRAME_PARAMETER = "FRAME_PARAMETER";

    @Inject
    private TabSheet tabSheet;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        initTabSheets();

    }

    /**
     * initialize tabs in table
     */
    private void initTabSheets() {
        createTaskableEntityTab();
        createTasksTab(MYTASK);
        createTasksTab(CONTROLTASK);
    }

    /**
     * create tab "My Task"
     * condition: right to taskable-entity-frame
     */
    private void createTaskableEntityTab() {
        TabSheet.Tab tab = tabSheet.addTab(TASKABLE_ENTITY_NAME, openFrame(null, TASKABLE_ENTITY_FRAME, ParamsMap.of(FRAME_PARAMETER, TASKABLE_ENTITY_NAME)));
        tab.setCaption(getMessage(TASKABLE_ENTITY_NAME));
    }

    /**
     * create tab
     *
     * @param name - name of frame
     */
    private void createTasksTab(String name) {
        TabSheet.Tab tab = tabSheet.addTab(name, openFrame(null, TASK_LIST_FRAME, ParamsMap.of(FRAME_PARAMETER, name)));
        tab.setCaption(getMessage("taskList." + name));
    }

    @Override
    public void ready() {
        super.ready();

        initTabSelection();
    }

    /**
     * restore active tab if it was saved
     * add handler will save active tab
     */
    private void initTabSelection() {
        Element element = getSettings().get(tabSheet.getId());
        String tabName = element.attributeValue("q_tab");
        if (!StringUtils.isEmpty(tabName)) {
            TabSheet.Tab tab = tabSheet.getTab(tabName);
            if (tab != null) {
                tabSheet.setSelectedTab(tab);
            }
        }

        tabSheet.addSelectedTabChangeListener(event -> {
            String currentTabName = event.getSelectedTab() == null ? null : event.getSelectedTab().getName();
            element.addAttribute("q_tab", currentTabName);
        });
    }

}