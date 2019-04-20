package com.groupstp.cifra.web.document.workflow.actions;

import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.WorkflowInstanceTask;
import com.groupstp.workflowstp.exception.WorkflowException;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;

public class CoordinateAction extends ItemTrackingAction {

    private Logger log = LoggerFactory.getLogger(CoordinateAction.class);

    private WorkflowService workflowService;

//    private HashMap<String, String> tabToNextTab;

    public CoordinateAction(ListComponent target, WorkflowService workflowService) {
        super(target, "coordinateAction");
        setIcon(CubaIcon.OK.source());
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.coordinate"));
        target.addAction(this);
        this.workflowService = workflowService;
//        tabToNextTab = new HashMap<String, String>() {{
//            put("юристы", "финансисты");
//            put("финансисты", "бухгалтеры");
//            put("бухгалтеры", "директор");
//            put("директор", "бухгалтерподоговорам");
//        }};
    }

    @Override
    public void actionPerform(Component component){
        super.actionPerform(component);
        for (Object doc:
            target.getSelected()) {
            Document document = (Document) doc;
            WorkflowInstanceTask wit = workflowService.getWorkflowInstanceTask(document);
            try {
                workflowService.finishTask(wit);
            } catch (WorkflowException e) {
                log.error("Coordinate action perform", e);
            }
        }
        // обновить таблицу в текущей вкладке
        target.getDatasource().refresh();
    }
}
