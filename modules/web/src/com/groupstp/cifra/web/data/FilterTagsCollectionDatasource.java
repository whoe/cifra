package com.groupstp.cifra.web.data;

import com.groupstp.cifra.entity.Tag;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.GroupDatasourceImpl;

import java.util.Map;
import java.util.UUID;


public class FilterTagsCollectionDatasource extends GroupDatasourceImpl<Tag, UUID> {

    @Override
    protected void loadData(Map<String, Object> params) {
        detachListener(data.values());
     //   data.clear();
    }

    @Override
    public void addItem(Tag item) {
        super.addItem(item);
    }
}
