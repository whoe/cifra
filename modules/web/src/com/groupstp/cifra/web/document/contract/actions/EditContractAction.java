package com.groupstp.cifra.web.document.contract.actions;

import com.groupstp.cifra.web.document.contract.ContractEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;

public class EditContractAction extends ItemTrackingAction {

    public EditContractAction(ListComponent target) {
        super(target, "editContractAction");
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.contract", "button.edit"));
        setIcon("icons/edit.png");
        target.addAction(this);
    }

    @Override
    public void actionPerform(Component component) {
        super.actionPerform(component);
        target.getFrame().openEditor("cifra$DocumentContract.edit",
                target.getSingleSelected(),
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("openType", ContractEdit.OpenType.edit)
        );
    }
}
