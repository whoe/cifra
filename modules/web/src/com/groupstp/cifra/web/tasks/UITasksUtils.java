package com.groupstp.cifra.web.tasks;

import com.groupstp.cifra.entity.tasks.*;
import com.groupstp.cifra.web.entity.CifraUiEvent;
import com.groupstp.cifra.web.tasks.task.TaskEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.Iterator;

public class UITasksUtils extends AbstractFrame{

    //Singleton
    public static final UITasksUtils INSTANCE = new UITasksUtils();

    private final DataManager dataManager = AppBeans.get(DataManager.class);
    private final Metadata metadata = AppBeans.get(Metadata.class);


    /**
     * @param startDate
     * @param interval     Integer
     * @param intervalType Hour,Day,Month....
     * @return - startDate + interval of intervalType (example: 01.01.2018 + 3 of Day = 04.01.2018)
     */
    public static Date countEndDateFromStartDate(Date startDate, Integer interval, IntervalType intervalType) {
        if (startDate == null || interval == null || intervalType == null) return null;

        switch (intervalType) {
            case Days:
                return DateUtils.addDays(startDate, interval);
            case Weeks:
                return DateUtils.addWeeks(startDate, interval);
            case Months:
                return DateUtils.addMonths(startDate, interval);
        }
        return null;
    }

    /**
     * Make the button from Strategy and add it to screen
     *
     * @param cubaIcon     - example CubaIcon.CHECK.source()
     * @param action       - Pattern Strategy
     * @param capture
     * @param buttonsPanel
     */
    public void makeButton(CubaIcon cubaIcon, BaseAction action, String capture, ButtonsPanel buttonsPanel) {
        Button button = AppBeans.get(ComponentsFactory.class).createComponent(Button.class);
        button.setAction(action);
        button.setCaption(capture);
        button.setIcon(cubaIcon.source());
        buttonsPanel.add(button);
    }

    /**
     * Create tasks from sampleTask, copy all fields except taskTypical and endDate
     *
     * @param iteratorTemplate iterator of typical tasks from template
     * @param sampleTask       Sample task
     */
    public void createTaskWithoutWindows(Iterator<TaskTypical> iteratorTemplate, Task sampleTask) {

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
            CifraUiEvent.push("taskCommitted");
        }
    }

    /**
     * Create one task fot document and open windows form
     *
     * @param document
     * @param iteratorTemplate
     * @return
     */
    public TaskEdit createTaskInWindows(TaskableEntity document, Iterator<TaskTypical> iteratorTemplate, Frame frame) {
        TaskTypical taskTypical;
        taskTypical = iteratorTemplate.next();
        Task newTask = metadata.create(Task.class);

        return (TaskEdit) frame.openEditor(newTask, WindowManager.OpenType.DIALOG, ParamsMap.of("taskTypical", taskTypical, "taskableEntity", document));
    }

}
