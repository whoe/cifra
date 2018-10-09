package com.groupstp.cifra.web.tasks.task;

import com.groupstp.cifra.entity.Employee;
import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskStatus;
import com.groupstp.cifra.entity.tasks.TaskTypical;
import com.groupstp.cifra.entity.tasks.TaskableEntity;
import com.groupstp.cifra.web.entity.CifraUiEvent;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

import static com.groupstp.cifra.web.tasks.UITasksUtils.countEndDateFromStartDate;

public class TaskEdit extends AbstractEditor<Task> {

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private DataManager dataManager;

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
            ((PickerField) fieldGroup.getComponent("taskableEntity")).setValue(taskableEntity.getTaskableEntity());
            ((LookupField) fieldGroup.getComponent("status")).setValue(TaskStatus.Assigned);

            User currentUser = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
            Employee currentEmployee = dataManager.load(LoadContext.create(Employee.class).setQuery(LoadContext.createQuery(
                    "select e from cifra$Employee e where e.user.id=:id"
            ).setParameter("id", currentUser.getId())));

            ((PickerField) fieldGroup.getComponent("author")).setValue(currentEmployee);

        }
    }

    /**
     * open document for current task
     */
    public void onOpenDocumentClick() {
        openEditor(((PickerField) fieldGroup.getComponent("taskableEntity")).getValue(), WindowManager.OpenType.NEW_WINDOW);
    }

    @Override
    public void commitAndClose() {
        super.commitAndClose();
        CifraUiEvent.push("taskCommitted");
    }
}