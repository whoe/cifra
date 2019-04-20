package com.groupstp.cifra.web.document.workflow;

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
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class RegisterHelperWindow extends AbstractWindow {

    private Workflow workflow;

    @Inject
    DataManager dataManager;

    @Inject
    private UserSessionSource userSessionSource;

    private String frame;

    private HashSet<UUID> allowedRolesForMyContracts;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        allowedRolesForMyContracts = new HashSet<>();
        // WorkFlow: все Шаги
        allowedRolesForMyContracts.add(UUID.fromString("4541b2a3-c836-c007-c963-83cc117ec3ca"));
        // Сотрудник компании
        allowedRolesForMyContracts.add(UUID.fromString("1422bdb4-b49b-237d-49b7-6cbd4135ccb2"));
        // Administrators
        allowedRolesForMyContracts.add(UUID.fromString("0c018061-b26f-4de2-a5be-dff348347f93"));
    }
// private Logger log = LoggerFactory.getLogger(RegisterHelperWindow.class);

    protected void initTabSheets(TabSheet tabs, String workflowCode) {
        workflow = getRegisterWorkflowByCode(workflowCode);
        if (workflow != null && !CollectionUtils.isEmpty(workflow.getSteps())) {
            for (Step step : workflow.getSteps()) {
                if (isSatisfyByUser(step.getStage())) {
                    String stageName = step.getStage().getName();
                    String tabKey = stageName.replaceAll("\\s", StringUtils.EMPTY).toLowerCase();

                    TabSheet.Tab tab = tabs.addTab(tabKey, createTab(step.getStage()));
                    tab.setCaption(stageName);
                }
            }
        }
    }

    /**
     * set frame for tabs
     * @param frame frame for tabs
     */
    protected void setFrame(String frame) {
        this.frame = frame;
    }

    private Component createTab(@Nullable Stage stage) {
        if (frame == null) {
            frame = "cifra$RegisterWorkflow.frame";
        }

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

    private Workflow getRegisterWorkflowByCode(String code) {
        return dataManager.load(Workflow.class)
                .query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.code = :code")
                .parameter("code", code)
                .view("workflow-incoming-outgoing-register")
                .one();
    }

    private User getUser() {
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

    /**
     * Возвращает true если пользователь имеет роль Сотрудник компании, Администратор, или Workflow-Все шаги
     * @return boolean
     */
    protected boolean isEmployee() {
        User user = getUser();
        for (UserRole ur:
            user.getUserRoles()) {
            if (allowedRolesForMyContracts.contains(ur.getRole().getId()))
                return true;
        }

        return false;
    }
}
