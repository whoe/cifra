package com.groupstp.cifra.web.screens;

import com.groupstp.cifra.entity.Sync1CService;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.ResizableTextArea;

import javax.inject.Inject;
import com.haulmont.cuba.gui.components.AbstractWindow;

public class Synchronization extends AbstractWindow {

    @Inject
    private Sync1CService sync1CService;

    @Inject
    private ResizableTextArea log;

    public void onTestSyncClick() {
        try {
            sync1CService.getData1C("accnt2016", "cifra", "1234567890123456");
        }
        catch (Exception e)
        {
            String t = log.getValue();
            t+="\n"+e.getLocalizedMessage();
            log.setValue(t);
        }
    }
}