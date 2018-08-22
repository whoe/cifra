package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Employee;
import com.groupstp.cifra.entity.DocType;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.UUID;

public class Newissue extends AbstractWindow {

    @Inject
    private LookupField employee;
    
    @Inject
    private LookupField status;
    
    
    public Employee getEmployee() {
        return employee.getValue();
    }
    
    public DocType getStatus() {
        return status.getValue();
    }

    public void confirm(Component source) {
        close(Window.COMMIT_ACTION_ID);
    }

    public void cancel(Component source) {
        close(Window.CLOSE_ACTION_ID);
    }
}