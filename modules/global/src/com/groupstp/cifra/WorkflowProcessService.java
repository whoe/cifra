package com.groupstp.cifra;


import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.Workflow;
import com.groupstp.workflowstp.entity.WorkflowEntity;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WorkflowProcessService {
    String NAME = "cifra_WorkflowProcessService";

    Workflow getActiveWorkflow();

    Workflow getActiveWorkflow(Class<? extends WorkflowEntity> entityClass);

    List<WorkflowInstanceTask> loadTasks(final Document document);

    void finishTask(WorkflowInstanceTask task, @Nullable Map<String, String> params) throws WorkflowException;

    UUID startWorkflow(WorkflowEntity entity, Workflow wf) throws WorkflowException;

    WorkflowInstanceTask loadLastOpenTask(Document document);

}