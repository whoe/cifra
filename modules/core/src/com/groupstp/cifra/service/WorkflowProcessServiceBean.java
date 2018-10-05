package com.groupstp.cifra.service;

import com.groupstp.cifra.Utils;
import com.groupstp.cifra.WorkflowProcessService;
import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.tasks.Task;
import com.groupstp.cifra.entity.tasks.TaskStatus;
import com.groupstp.cifra.entity.tasks.TaskableEntity;
import com.groupstp.cifra.listener.TaskEntityListener;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service(WorkflowProcessService.NAME)
public class WorkflowProcessServiceBean implements WorkflowProcessService {

    private static final Logger log = LoggerFactory.getLogger(TaskEntityListener.class);

    @Inject
    Metadata metadata;

    @Inject
    DataManager dataManager;

    @Inject
    WorkflowService workflowService;

    /**
     * Return one active workflow for predefined Document.class
     *
     * @return active workflow for Class
     */
    public Workflow getActiveWorkflow() {
        return getActiveWorkflow(Document.class);
    }

    /**
     * Return one active workflow for Class or return null
     *
     * @return active workflow for Class
     */
    public Workflow getActiveWorkflow(Class<? extends WorkflowEntity> entityClass) {

        String entityName = metadata.getClassNN(entityClass).getName();
        List<Workflow> list = dataManager.loadList(LoadContext.create(Workflow.class)
                .setQuery(new LoadContext.Query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.entityName = :entityName order by e.createTs asc")
                        .setParameter("entityName", entityName))
                .setView("workflow-browse"));
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                log.warn(String.format("In system existing two active workflow for entityClass '%s'. The first will be used", entityName));
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * Load all tasks for document for workflow
     *
     * @param document
     * @param workflow
     * @return list of tasks, if no founded return empty list
     */
    public List<WorkflowInstanceTask> loadTasks(final Document document, final Workflow workflow) {
        if (workflow == null) return Collections.emptyList();
        return dataManager.loadList(LoadContext.create(WorkflowInstanceTask.class)
                .setQuery(new LoadContext.Query("select e from wfstp$" + "WorkflowInstanceTask e " +
                        "join e.instance i " +
                        "where i.workflow.id = :workflowId and i.entityId = :entityId " +
                        " order by e.createTs desc")
                        .setParameter("workflowId", workflow.getId())
                        .setParameter("entityId", document.getId().toString())
                        .setMaxResults(1))
                .setView("workflowInstanceTask-browse"));
    }

    @Override
    public void finishTask(WorkflowInstanceTask task, @Nullable Map<String, String> params) throws WorkflowException {
        workflowService.finishTask(task, params);
    }

    @Override
    public UUID startWorkflow(WorkflowEntity entity, Workflow wf) throws WorkflowException {
        return workflowService.startWorkflow(entity, wf);
    }

    @Override
    public WorkflowInstanceTask loadLastTask(Document document, Workflow activeWorkflow) {
        List<WorkflowInstanceTask> tasks = loadTasks(document, activeWorkflow);
        for (WorkflowInstanceTask task : tasks) {
            if (task.getEndDate() == null) return task;
        }
        return null;
    }

    @Override
    public void processWorkflowWithTask(Task task) {
        TaskableEntity taskableEntity = task.getTaskableEntity();
        if (taskableEntity == null) return;

        Document document = dataManager.load(Document.class).id(taskableEntity.getId()).one();

        WorkflowInstanceTask lastTask = loadLastTask(document, getActiveWorkflow());
        if (lastTask == null) return;

        String nameOfLastStep = lastTask.getStep().getStage().getName();

        try {
            if (Utils.STEP_PROCESSING_NAME.equals(nameOfLastStep) && isItActiveTaskForDocument(document)) {
                workflowService.finishTask(lastTask, Collections.singletonMap("doc_has_tasks", "true"));
            } else if (Utils.STEP_WORK_NAME.equals(nameOfLastStep) && !isItActiveTaskForDocument(document)) {
                workflowService.finishTask(lastTask, Collections.singletonMap("doc_has_tasks", "false"));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка обработки заявки", ex);
        }
    }

    @Override
    public void processWorkflowWithTask(Document document) {
        if (!isItActiveTaskForDocument(document)) {
            WorkflowInstanceTask lastTask = loadLastTask(document, getActiveWorkflow());
            if (lastTask == null) return;
            try {
                workflowService.finishTask(lastTask, Collections.singletonMap("doc_has_tasks", "false"));
            } catch (Exception ex) {
                throw new RuntimeException("Ошибка обработки заявки", ex);
            }
        }

    }

    private boolean isItActiveTaskForDocument(Document document) {

        List<Task> tasks = dataManager.loadList(LoadContext.create(Task.class).setQuery(
                LoadContext.createQuery("select t from tasks$Task t where t.taskableEntity.id=:docId and (t.status=:taskStatusRunning or t.status=:taskStatusAssigned)")
                        .setParameter("docId", document.getId())
                        .setParameter("taskStatusAssigned", TaskStatus.Assigned)
                        .setParameter("taskStatusRunning", TaskStatus.Running)));

        return !tasks.isEmpty();
    }

}