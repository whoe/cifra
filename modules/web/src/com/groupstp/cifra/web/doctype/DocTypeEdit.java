package com.groupstp.cifra.web.doctype;

import com.groupstp.cifra.entity.CheckListItems;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.groupstp.cifra.entity.DocType;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.DataGrid;
import org.eclipse.persistence.internal.sessions.DirectCollectionChangeRecord;

import javax.inject.Inject;
import javax.lang.model.type.NullType;

public class DocTypeEdit extends AbstractEditor<DocType> {

    @Inject
    private DataGrid<CheckListItems> checkListItemsGrid;

    public void onCreate(Component source) {
        CheckListItems item = new CheckListItems();
        item.setItem("<>");
        item.setDocType(this.getItem());
        DataManager manager = AppBeans.get(DataManager.class);
        manager.commit(item);

        this.getDsContext().refresh();
    }
}