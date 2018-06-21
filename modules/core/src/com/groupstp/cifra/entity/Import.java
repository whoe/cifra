package com.groupstp.cifra.entity;

import com.google.gson.JsonObject;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;

import javax.inject.Inject;
import javax.swing.text.html.parser.Entity;

public abstract class Import {
    public void updateInsertEntity(Class type, String id, JsonObject o) throws Exception {
        LoadContext<StandardEntity> c = LoadContext.create(type).setQuery(
                LoadContext.createQuery("select c from cifra$"+type.getSimpleName()+" c where c.externalId=:id")
                        .setParameter("id", id)
        );
        StandardEntity e = AppBeans.get(DataManager.class).load(c);
        if(e==null)
            insert(id, o);
        else
            update(id, o);
    }

    abstract void update(String id, JsonObject o);
    abstract void insert(String id, JsonObject o) throws Exception;
}

