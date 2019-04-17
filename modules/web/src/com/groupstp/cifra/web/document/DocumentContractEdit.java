package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Direction;
import com.groupstp.cifra.entity.DocType;
import com.groupstp.cifra.entity.Document;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebAbstractField;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.UUID;

public class DocumentContractEdit extends AbstractEditor<Document> {

    @Inject
    FieldGroup fieldGroup;

    @Named("fieldGroup.docType")
    WebAbstractField docType;

    @Inject
    Datasource<Document> documentDs;

    @Inject
    CollectionDatasource<DocType, UUID> docTypesDs;

    @WindowParam
    private OpenType openType;


    @Override
    public void ready() {

        // todo now direction not null. delete or something else
        Document doc = documentDs.getItem();
        doc.setDirection(Direction.Income);

        if (openType.equals(OpenType.create)) {
            docTypesDs.refresh();
            DocType contractType = docTypesDs.getItem(UUID.fromString("60a9a602-d986-bbb7-fcc7-ad0e3e07d6de"));
            docType.setValue(contractType);
            doc.setDocType(contractType);
        }
        if (openType.equals(OpenType.browse)) {
            fieldGroup.setEditable(false);
        }
    }

    public enum OpenType {
        create,
        edit,
        browse
    }
}