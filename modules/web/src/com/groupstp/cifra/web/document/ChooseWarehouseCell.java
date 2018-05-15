package com.groupstp.cifra.web.document;

import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;

public class ChooseWarehouseCell extends AbstractWindow {

    @Inject
    private LookupPickerField warehouse;

    @Inject
    private TextField cell;

    public LookupPickerField getWarehouse() {
        return warehouse;
    }

    public TextField getCell() {
        return cell;
    }

    public void onOk(Component source) {
        close(Window.COMMIT_ACTION_ID);
    }

    public void onCancel(Component source)   {
        close(Window.CLOSE_ACTION_ID);
    }
}