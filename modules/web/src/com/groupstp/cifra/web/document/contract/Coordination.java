package com.groupstp.cifra.web.document.contract;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.web.document.contract.actions.*;
import com.groupstp.cifra.web.document.workflow.WorkflowHelperWindow;
import com.groupstp.workflowstp.entity.WorkflowEntityStatus;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class Coordination extends WorkflowHelperWindow {
    @Inject
    TabSheet tabs;

    @Inject
    private Button irrelevantBtn;

    @Inject
    private Button createBtn;

    @Inject
    private Button editBtn;

    @Inject
    private Button toWorkBtn;

    @Inject
    private Button removeBtn;

    @Inject
    private Button historyBtn;

    @Inject
    private GroupTable<Document> contracts;

    @Inject
    private WorkflowService workflowService;

    @Inject
    private GroupDatasource<Document, UUID> documentsDs;

    private HashSet<UUID> allowedRolesForMyContracts;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initTabSheets(tabs,
                "coord",
                "cifra$ContractWorkflow.frame"
        );

        allowedRolesForMyContracts = new HashSet<>();
        // WorkFlow: все Шаги
        allowedRolesForMyContracts.add(UUID.fromString("4541b2a3-c836-c007-c963-83cc117ec3ca"));
        // Сотрудник компании
        allowedRolesForMyContracts.add(UUID.fromString("1422bdb4-b49b-237d-49b7-6cbd4135ccb2"));

        if (!isEmployee()) {
            tabs.getTab("myContracts").setVisible(false);
            return;
        }

        Action editAction = new EditContractAction(contracts);
        editBtn.setAction(editAction);

        Action createAction = new CreateContractAction(contracts);
        createBtn.setAction(createAction);

        IrrelevantAction irrelevantAction = new IrrelevantAction(contracts, workflowService);
        irrelevantBtn.setAction(irrelevantAction);

        Action toWorkAction = new ToWorkAction(contracts, workflowService);
        toWorkBtn.setAction(toWorkAction);

        HistoryAction historyAction = new HistoryAction(contracts);
        historyBtn.setAction(historyAction);

        removeBtn.setEnabled(false);

        // кнопка удалить неактивна если статус в процессе или завершена
        documentsDs.addItemChangeListener(doc -> {
            if (doc == null || doc.getItem() == null) return;
            WorkflowEntityStatus status = doc.getItem().getStatus();
            if (WorkflowEntityStatus.IN_PROGRESS.equals(status))
                removeBtn.setEnabled(false);
            else
                removeBtn.setEnabled(true);
        });

    }

    /**
     * Возвращает true если пользователь имеет роль Сотрудник компании, Администратор, или Workflow-Все шаги
     *
     * @return boolean
     */
    private boolean isEmployee() {
        User user = getUser();
        for (UserRole ur :
                user.getUserRoles()) {
            if (allowedRolesForMyContracts.contains(ur.getRole().getId()))
                return true;
        }

        return false;
    }
}