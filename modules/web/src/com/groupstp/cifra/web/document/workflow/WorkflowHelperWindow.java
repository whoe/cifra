package com.groupstp.cifra.web.document.workflow;

import com.groupstp.cifra.web.document.contract.ContractWorkflowFrame;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.StageType;
import com.groupstp.workflowstp.entity.Step;
import com.groupstp.workflowstp.entity.Workflow;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.TabSheet;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

public class WorkflowHelperWindow extends AbstractWindow {

    private Workflow workflow;

    @Inject
    DataManager dataManager;

    @Inject
    private UserSessionSource userSessionSource;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }


    protected void initTabSheets(TabSheet tabs, String workflowCode, String workflowFrame) {
        workflow = getWorkflowByCode(workflowCode);
        if (workflow != null && !CollectionUtils.isEmpty(workflow.getSteps())) {
            for (Step step : workflow.getSteps()) {
                if (isSatisfyByUser(step.getStage())) {
                    String stageName = step.getStage().getName();
                    String tabKey = stageName.replaceAll("\\s", StringUtils.EMPTY).toLowerCase();
                    TabSheet.Tab tab = tabs.addTab(tabKey, createTab(workflowFrame, step.getStage()));
                    tab.setCaption(stageName);
                }
            }
        }

        // обновление таблицы выбранной вкладки
        // todo delete, probably should use CifraUiEvent
        tabs.addSelectedTabChangeListener(event -> {
            try {
                TabSheet.Tab selectedTab = event.getSelectedTab();
                if (selectedTab == null) {
                    return;
                }
                String tabName = selectedTab.getName();
                Component tabComponent = tabs.getTabComponent(tabName);
                ContractWorkflowFrame frame = (ContractWorkflowFrame) tabComponent;
                Component table = frame.getComponent("documentsTable");
                if (table != null) {
                    ((Table) table).getDatasource().refresh();
                }
            } catch(ClassCastException e) {
                // do nothing, changed tab to myContracts
            }
        });
    }

    private Component createTab(@Nullable String frame, @Nullable Stage stage) {
        return openFrame(null, frame,
                ParamsMap.of(DocumentWorkflowFrame.STAGE, stage,
                        DocumentWorkflowFrame.WORKFLOW, workflow));
    }

    private boolean isSatisfyByUser(Stage stage) {
        User user = getUser();
        if (stage != null) {
            if (StageType.USERS_INTERACTION.equals(stage.getType())) {
                if (!CollectionUtils.isEmpty(stage.getActorsRoles())) {
                    if (!CollectionUtils.isEmpty(user.getUserRoles())) {
                        for (UserRole ur : user.getUserRoles()) {
                            if (stage.getActorsRoles().contains(ur.getRole())) {
                                return true;
                            }
                        }
                    }
                } else if (!CollectionUtils.isEmpty(stage.getActors())) {
                    return stage.getActors().contains(user);
                }
            }
        }
        return false;
    }

    private Workflow getWorkflowByCode(String code) {
        return dataManager.load(Workflow.class)
                .query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.code = :code")
                .parameter("code", code)
                .view("workflow-incoming-outgoing-register")
                .one();
    }

    protected User getUser() {
        User user = userSessionSource.getUserSession().getCurrentOrSubstitutedUser();
        if (user == null) {
            throw new DevelopmentException(getMessage("documentsWorkflowBrowse.reloadedUserNotFound"));
        }
        user = dataManager.reload(user, "user-with-role");
        if (user == null) {
            throw new DevelopmentException(getMessage("documentsWorkflowBrowse.reloadedUserNotFound"));
        }
        return user;
    }

}
