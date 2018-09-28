package com.groupstp.cifra.entity;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
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

    @Inject
    private Metadata metadata;

    private Logger log = LoggerFactory.getLogger(getClass());

    public List<CheckList> fillCheckList(Document doc)
    {
        Preconditions.checkNotNullArgument(doc);

        DocType dt = doc.getDocType();
        List<CheckList> newItems = new ArrayList<>();
        if(dt==null)
            return newItems;

        LoadContext<CheckListItems> checkListItemsLoadContext = new LoadContext<>(CheckListItems.class);
        checkListItemsLoadContext.setQueryString("select i from cifra$CheckListItems i where i.docType.id=:dt")
                .setParameter("dt", dt.getId());
        List<CheckListItems> items = dataManager.loadList(checkListItemsLoadContext);

        for (CheckListItems item : items) {
            CheckList newitem = metadata.create(CheckList.class);
            newitem.setDocument(doc);
            newitem.setItem(item);
            newItems.add(newitem);
        }
        return newItems;
    }

}