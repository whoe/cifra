package com.groupstp.cifra.entity;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service(CheckListService.NAME)
public class CheckListServiceBean implements CheckListService {

    @Inject
    private DataManager dataManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    public List<CheckList> fillCheckList(Document doc)
    {
        DocType dt = doc.getDocType();
        LoadContext<CheckListItems> checkListItemsLoadContext = new LoadContext<>(CheckListItems.class);
        checkListItemsLoadContext.setQueryString("select i from cifra$CheckListItems i where i.docType.id=:dt")
                .setParameter("dt", dt.getId());
        List<CheckListItems> items = dataManager.loadList(checkListItemsLoadContext);

        LoadContext<CheckListItems> docCheckListItemsLoadContext = new LoadContext<>(CheckListItems.class);
        docCheckListItemsLoadContext.setQueryString("select i.item from cifra$CheckList i where i.document.id=:doc")
                .setParameter("doc", doc.getId());
        List<CheckListItems> docitems = dataManager.loadList(docCheckListItemsLoadContext);

        List<CheckList> newItems = new ArrayList<>();
        for (CheckListItems item : items) {
            if(docitems.contains(item))
                continue;
            CheckList newitem = new CheckList();
            newitem.setDocument(doc);
            newitem.setItem(item);
            newItems.add(newitem);
        }
        return newItems;
    }

    public void clearCheckList(Document doc)
    {
        DataManager itemsDm = AppBeans.get(DataManager.class);
        List<CheckList> list = doc.getChecklist();
        if(list==null)
            return;
        list.forEach(item->itemsDm.remove(item));
    }
}