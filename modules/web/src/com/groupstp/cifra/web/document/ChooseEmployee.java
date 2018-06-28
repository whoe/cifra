package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Employee;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.UUID;

public class ChooseEmployee extends AbstractWindow {

    @Inject
    private LookupPickerField employee;

    public Employee getEmployee()
    {
        return  employee.getValue();
    }

    public void onOk(Component source) {
        close(Window.COMMIT_ACTION_ID);
    }

    public void onCancel(Component source)   {
        close(Window.CLOSE_ACTION_ID);
    }
}
