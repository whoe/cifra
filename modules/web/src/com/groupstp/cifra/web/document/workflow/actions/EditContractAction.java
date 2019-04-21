package com.groupstp.cifra.web.document.workflow.actions;

import com.groupstp.cifra.web.document.DocumentContractEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;

public class EditContractAction extends ItemTrackingAction {

    private ListComponent target;
    public EditContractAction(ListComponent target) {
        super(target, "editContractAction");
        this.target = target;
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.edit"));
        setIcon("icons/edit.png");
        target.addAction(this);
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        target.getFrame().openEditor("cifra$DocumentContract.edit",
                target.getSingleSelected(),
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("openType", DocumentContractEdit.OpenType.edit)
        );
    }
}
