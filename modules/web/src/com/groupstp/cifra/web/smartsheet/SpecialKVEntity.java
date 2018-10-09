package com.groupstp.cifra.web.smartsheet;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.data.impl.AbstractCollectionDatasource;

@com.haulmont.chile.core.annotations.MetaClass(name = "cifra$SpecialKVEntity")
public class SpecialKVEntity extends KeyValueEntity {

    private AbstractCollectionDatasource datasource;

    public SpecialKVEntity(AbstractCollectionDatasource ds) {
        datasource = ds;
    }

    /**
     * Sets a meta-class for this entity instance.
     *
     * @param ignored - ignored
     */
    @Override
    public void setMetaClass(MetaClass ignored) {
    }


    @Override
    public MetaClass getMetaClass() {
        MetaClass metaClass = datasource.getMetaClass();
        metaClass.getProperties().clear();
        return metaClass;
    }

}
