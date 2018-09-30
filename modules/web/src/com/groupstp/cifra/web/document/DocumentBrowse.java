package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.Tag;
import com.groupstp.cifra.web.document.workflow.DocumentWorkflowFrame;
import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.StageType;
import com.groupstp.workflowstp.entity.Step;
import com.groupstp.workflowstp.entity.Workflow;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DocumentBrowse extends AbstractLookup {

    private static final Logger log = LoggerFactory.getLogger(DocumentBrowse.class);

    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;

    @Named("tagsDs")
    private CollectionDatasource<Tag, UUID> optionsTagsDs;

    @Named("tabs")
    private TabSheet tabSheet;

    @Named("income.edit")
    private EditAction incomeEditAction;

    @Named("filter")
    private Filter filter;

    @Named("topTagsContainer")
    private BoxLayout tagsContainer;

    @Inject
    private UserSessionSource userSessionSource;

    private Workflow activeWorkflow;
    private User user;


    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        activeWorkflow = getActiveWorkflow();
        user = getUser();

        initTabSheets();
    }


    //initialize tabs view which dependents on active workflow
    private void initTabSheets() {
        if (activeWorkflow != null && !CollectionUtils.isEmpty(activeWorkflow.getSteps())) {
            for (Step step : activeWorkflow.getSteps()) {
                if (isSatisfyByUser(step.getStage())) {
                    String stageName = step.getStage().getName();
                    String tabKey = stageName.replaceAll("\\s", StringUtils.EMPTY).toLowerCase();

                    TabSheet.Tab tab = tabSheet.addTab(tabKey, createTab(step.getStage()));
                    tab.setCaption(stageName);
                }
            }
        }
    }

    //check what is current user is actor of this stage
    private boolean isSatisfyByUser(Stage stage) {
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

    private Component createTab(@Nullable Stage stage) {
        return openFrame(null, "cifra$DocumentWorkflow.frame",
                ParamsMap.of(DocumentWorkflowFrame.STAGE, stage,
                        DocumentWorkflowFrame.WORKFLOW, activeWorkflow));
    }

    @Override
    public void ready() {
        super.ready();

        initTabSelection();
    }

    private void initTabSelection() {
        Element element = getSettings().get(tabSheet.getId());
        String tabName = element.attributeValue("q_tab");
        if (!StringUtils.isEmpty(tabName)) {
            TabSheet.Tab tab = tabSheet.getTab(tabName);
            if (tab != null) {
                tabSheet.setSelectedTab(tab);
            }
        }

        tabSheet.addSelectedTabChangeListener(event -> {
            String currentTabName = event.getSelectedTab() == null ? null : event.getSelectedTab().getName();
            element.addAttribute("q_tab", currentTabName);
        });
    }

    //retrieve one active workflow for workflow entity
    @Nullable
    private Workflow getActiveWorkflow() {
        String entityName = metadata.getClassNN(Document.class).getName();
        List<Workflow> list = dataManager.loadList(LoadContext.create(Workflow.class)
                .setQuery(new LoadContext.Query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.entityName = :entityName order by e.createTs asc")
                        .setParameter("entityName", entityName))
                .setView("workflow-browse"));
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                log.warn(String.format("In system existing two active workflow for entity '%s'. The first will be used", entityName));
            }
            return list.get(0);
        }
        return null;
    }

    //get current user
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
}


