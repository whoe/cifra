package com.groupstp.cifra.web.tasks.tasktypical;

import com.groupstp.cifra.entity.tasks.TaskTypical;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;

import javax.inject.Inject;
import java.util.Map;

public class TaskTypicalBrowse extends AbstractLookup {

    public static final String MULTIPLE_SELECT_ON = "MULTIPLE_SELECT_ON";

    @Inject
    Table<TaskTypical> taskTypicalTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("MULTIPLE_SELECT_ON")) {
            taskTypicalTable.setMultiSelect(true);
        }
    }
}