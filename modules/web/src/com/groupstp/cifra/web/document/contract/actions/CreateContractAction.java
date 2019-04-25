package com.groupstp.cifra.web.document.contract.actions;

import com.groupstp.cifra.web.document.contract.ContractEdit;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.CreateAction;

public class CreateContractAction extends CreateAction {

    public CreateContractAction(ListComponent target) {
        super(target);
        setCaption(messages.getMessage("com.groupstp.cifra.web.document.contract", "button.create"));
        setWindowId("cifra$DocumentContract.edit");
        setWindowParams(ParamsMap.of("openType", ContractEdit.OpenType.create));
    }
}
