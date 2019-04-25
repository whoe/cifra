package com.groupstp.cifra.web.document.contract;

import com.groupstp.cifra.entity.ContractHistoryItem;
import com.groupstp.workflowstp.entity.WorkflowInstance;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ContractHistory extends AbstractWindow {

    @Inject
    DataManager dataManager;

    @Inject
    Metadata metadata;

    @Inject
    CollectionDatasource<ContractHistoryItem, UUID> contractHistoryItemsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setCaption(getMessage("history"));
        if (!params.containsKey("instance")) {
            return;
        }
        WorkflowInstance instance = (WorkflowInstance) params.get("instance");
        List<WorkflowInstanceTask> tasks = dataManager.load(WorkflowInstanceTask.class)
                .query("select it from wfstp$WorkflowInstanceTask it where it.instance.id = :instanceId order by it.updateTs")
                .parameter("instanceId", instance.getId())
                .view("workflowInstanceTask-contract-history-view")
                .list();

        ContractHistoryItem prevItem = null;
        for (WorkflowInstanceTask task :
                tasks) {
            String stageName = task.getStep().getStage().getName();
            if (prevItem != null) {
                prevItem.setTo(stageName);
            }
            ContractHistoryItem item = metadata.create(ContractHistoryItem.class);
            contractHistoryItemsDs.addItem(item);
            item.setFrom(stageName);

            if (task.getUpdatedBy() == null) {
                prevItem = item;
                continue;
            }
            User user = dataManager.load(User.class)
                    .query("select u from sec$User u where u.login = :login")
                    .parameter("login", task.getUpdatedBy())
                    .view("")
                    .one();
            item.setPerformer(user);
            item.setWhen(task.getEndDate());
            prevItem = item;
        }
    }
}