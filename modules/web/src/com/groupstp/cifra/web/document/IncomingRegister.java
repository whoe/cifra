package com.groupstp.cifra.web.document;

import com.groupstp.cifra.web.document.workflow.RegisterHelperWindow;
import com.haulmont.cuba.gui.components.TabSheet;

import javax.inject.Inject;
import java.util.Map;

/**
 * реестр электронных обращений
 */
public class IncomingRegister extends RegisterHelperWindow {

    @Inject
    TabSheet tabs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initTabSheets(tabs, "inreg");
    }

}