package com.groupstp.cifra.web.document.workflow.actions;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.EditAction;

public class BrowseAction extends EditAction {
    public BrowseAction(ListComponent target, WindowManager.OpenType openType) {
        super(target, openType);
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow","button.browse"));
    }

}
