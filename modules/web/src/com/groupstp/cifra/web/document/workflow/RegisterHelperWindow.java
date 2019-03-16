package com.groupstp.cifra.web.document.workflow;

import com.groupstp.workflowstp.entity.Stage;
import com.groupstp.workflowstp.entity.Step;
import com.groupstp.workflowstp.entity.Workflow;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.TabSheet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class RegisterHelperWindow extends AbstractWindow {

    private Workflow workflow;

    @Inject
    DataManager dataManager;

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

    private Component createTab(@Nullable Stage stage) {
        return openFrame(null, "cifra$RegisterWorkflow.frame",
                ParamsMap.of(DocumentWorkflowFrame.STAGE, stage,
                        DocumentWorkflowFrame.WORKFLOW, workflow));
    }

    private boolean isSatisfyByUser(Stage stage) {
        return true;
    }

    private Workflow getRegisterWorkflowByCode(String code) {
        return dataManager.load(Workflow.class)
                .query("select e from wfstp$Workflow e where " +
                        "e.active = true and e.code = :code")
                .parameter("code", code)
                .view("workflow-browse")
                .one();
    }
}
