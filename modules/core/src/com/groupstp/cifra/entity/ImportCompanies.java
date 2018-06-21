package com.groupstp.cifra.entity;

import com.google.gson.JsonObject;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;

public class ImportCompanies extends Import{

    @Override
    void update(String id, JsonObject o) {

    }

    @Override
    void insert(String id, JsonObject o) {
        Company com = AppBeans.get(Metadata.class).create(Company.class);
        com.setExternalId(id);
        com.setName(o.get("Наименование").getAsString());
        com.setFullName(o.get("НаименованиеПолное").getAsString());
        com.setInn(o.get("ИНН").getAsString());
        AppBeans.get(DataManager.class).commit(com);
    }
}
