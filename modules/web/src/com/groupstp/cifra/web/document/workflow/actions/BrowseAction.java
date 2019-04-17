package com.groupstp.cifra.web.document.workflow.actions;


import com.groupstp.cifra.web.document.DocumentContractEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.EditAction;

public class BrowseAction extends EditAction {

    public BrowseAction(ListComponent target, WindowManager.OpenType openType) {
        super(target, openType);
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.workflow", "button.browse"));
        setWindowId("cifra$DocumentContract.edit");
        target.addAction(this);
        setWindowParams(ParamsMap.of("openType", DocumentContractEdit.OpenType.browse));
    }
}
