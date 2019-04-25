package com.groupstp.cifra.web.document;

import com.groupstp.cifra.web.document.workflow.WorkflowHelperWindow;
import com.groupstp.cifra.web.entity.CifraUiEvent;
import com.haulmont.cuba.gui.components.TabSheet;
import org.dom4j.Element;
import org.springframework.context.event.EventListener;

import javax.inject.Named;
import java.util.Map;

public class DocumentBrowse extends WorkflowHelperWindow {


    @Named("tabs")
    private TabSheet tabSheet;

    @EventListener
    public void onCifraUiEvent(CifraUiEvent event) {
        if ("documentCommitted".equals(event.getSource())) {
            getDsContext().refresh();
        }
    }

    @Override
    public void init(Map<String, Object> params) {

        super.init(params);

        // activeWorkflow = getActiveWorkflow();
        // user = getUser();

    }


    @Override
    public void ready() {
        super.ready();
        initTabSheets(tabSheet,
                "document",
                "cifra$DocumentWorkflow.frame");


        Element element = getSettings().get(tabSheet.getId());
        tabSheet.addSelectedTabChangeListener(event -> {
            String currentTabName = event.getSelectedTab() == null ? null : event.getSelectedTab().getName();
            element.addAttribute("q_tab", currentTabName);
        });
    }
}


