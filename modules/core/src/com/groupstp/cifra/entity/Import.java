package com.groupstp.cifra.entity;

import com.google.gson.JsonObject;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;

import java.util.List;

abstract class Import {
    <T extends StandardEntity> void updateInsertEntity(Class<T> type, String id, JsonObject o) throws Exception {
        DataManager dataManager = AppBeans.get(DataManager.class);
        List<? extends StandardEntity> entities = dataManager.load(type)
                .query("select c from cifra$" + type.getSimpleName() + " c where c.externalId=:id")
                .parameter("id", id)
                .list();
        if (entities.size() >= 1)
            update(id, o);
        else
            insert(id, o);
    }

    abstract void update(String id, JsonObject o);

    abstract void insert(String id, JsonObject o) throws Exception;
}

