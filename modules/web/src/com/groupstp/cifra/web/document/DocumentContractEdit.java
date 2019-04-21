package com.groupstp.cifra.web.document;

import com.groupstp.cifra.entity.Direction;
import com.groupstp.cifra.entity.DocType;
import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.WorkflowInstance;
import com.groupstp.workflowstp.entity.WorkflowInstanceComment;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
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

    @Inject
    CollectionDatasource<WorkflowInstanceComment, UUID> workflowInstanceCommentsDs;

    @Override
    public void ready() {

        // todo now direction not null. delete or something else
        Document doc = documentDs.getItem();
        doc.setDirection(Direction.Income);

        if (openType.equals(OpenType.create)) {
            docTypesDs.refresh();
            DocType contractType = docTypesDs.getItem(UUID.fromString("60a9a602-d986-bbb7-fcc7-ad0e3e07d6de"));
            doc.setDocType(contractType);
        }

        docType.setValue(doc.getDocType());

        if (openType.equals(OpenType.browse)) {
            fieldGroup.setEditable(false);
        }

        WorkflowService service = AppBeans.get(WorkflowService.class);
        WorkflowInstance instance = service.getWorkflowInstance(doc);
        if (instance != null) {
            workflowInstanceCommentsDs.refresh(
                    ParamsMap.of("instanceId", instance.getId())
            );
        }
    }

    public enum OpenType {
        create,
        edit,
        browse
    }
}