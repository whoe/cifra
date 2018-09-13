package com.groupstp.cifra.web.doctype;

import com.groupstp.cifra.entity.CheckListItems;
import com.groupstp.cifra.entity.DocType;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.CollectionPropertyDatasourceImpl;

import javax.inject.Inject;
import java.util.UUID;

public class DocTypeEdit extends AbstractEditor<DocType> {

    @Inject
    private DataGrid<CheckListItems> checkListItemsGrid;

    @Inject
    private CollectionPropertyDatasourceImpl<CheckListItems, UUID> checkListItemsDs;

    @Inject
    private Datasource<DocType> docTypeDs;

    @Inject
    private EntityStates entityStates;

    @Inject
    private Metadata metadata;

    @Override
    public void ready() {
        super.ready();
        checkListItemsGrid.setEnabled(!entityStates.isNew(docTypeDs.getItem()));
    }


    public void onCreate(Component ignore) {
        CheckListItems item = metadata.create(CheckListItems.class);
        item.setItem("<>");
        item.setDocType(docTypeDs.getItem());
        checkListItemsDs.addItem(item);
    }
}