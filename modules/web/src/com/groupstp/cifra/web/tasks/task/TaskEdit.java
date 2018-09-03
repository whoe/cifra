package com.groupstp.cifra.web.tasks.task;

import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskStatus;
import com.groupstp.cifra.entity.tasks.TaskTypical;
import com.groupstp.cifra.entity.tasks.TaskableEntity;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

import static com.groupstp.cifra.web.tasks.task.Utils.countEndDateFromStartDate;

public class TaskEdit extends AbstractEditor<Task> {

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    UserSessionSource userSessionSource;

    private TaskTypical taskTypical;
    private TaskableEntity taskableEntity;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        taskTypical = (TaskTypical) params.get("taskTypical");
        taskableEntity = (TaskableEntity) params.get("taskableEntity");

        if (taskTypical != null && taskTypical.getInterval() > 0 && taskTypical.getIntervalType() != null) {

            ((DateField) fieldGroup.getComponent("startDate")).addValueChangeListener(e -> {

                Date endDate = countEndDateFromStartDate((Date) e.getValue(), taskTypical.getInterval(), taskTypical.getIntervalType());
                ((DateField) fieldGroup.getComponent("endDate")).setValue(endDate);
            });
        }
    }

    @Override
    public void ready() {
        super.ready();

        if (taskTypical != null) {
            ((PickerField) fieldGroup.getComponent("taskTypical")).setValue(taskTypical);
            ((TextField) fieldGroup.getComponent("taskTypicalDescription")).setValue(taskTypical.getDescription());
            ((TextField) fieldGroup.getComponent("taskableEntityName")).setValue(taskableEntity.getTaskableEntityName());
            ((TextField) fieldGroup.getComponent("taskableEntityId")).setValue(taskableEntity.getTaskableEntityEntityID());
            ((LookupField) fieldGroup.getComponent("status")).setValue(TaskStatus.Assigned);
//            ((PickerField) fieldGroup.getComponent("author")).setValue(userSessionSource.getUserSession().getCurrentOrSubstitutedUser());
        }
    }
}