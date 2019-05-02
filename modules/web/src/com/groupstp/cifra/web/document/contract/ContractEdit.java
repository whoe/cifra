package com.groupstp.cifra.web.document.contract;

import com.groupstp.cifra.entity.Direction;
import com.groupstp.cifra.entity.DocType;
import com.groupstp.cifra.entity.Document;
import com.groupstp.workflowstp.entity.WorkflowInstance;
import com.groupstp.workflowstp.entity.WorkflowInstanceComment;
import com.groupstp.workflowstp.service.WorkflowService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.web.gui.components.WebAbstractField;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class ContractEdit extends AbstractEditor<Document> {

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

    @Inject
    private FileMultiUploadField multiUpload;

    @Inject
    private FileUploadingAPI fileUploadingAPI;

    @Inject
    private DataSupplier dataSupplier;

    @Inject
    private CollectionDatasource<FileDescriptor, UUID> attachedFilesDs;

    @Inject
    private Table<FileDescriptor> filesTable;

    @Inject
    Button filesTableRemoveButton;

    @Override
    public void ready() {

        // todo: now direction not null. delete or something else
        Document doc = documentDs.getItem();
        doc.setDirection(Direction.Income);

        if (openType.equals(OpenType.create)) {
            docTypesDs.refresh();
            DocType contractType = docTypesDs.getItem(UUID.fromString("60a9a602-d986-bbb7-fcc7-ad0e3e07d6de"));
            doc.setDocType(contractType);
        }

        docType.setValue(doc.getDocType());


        WorkflowService service = AppBeans.get(WorkflowService.class);

        if (openType.equals(OpenType.browse) ||
                service.isProcessing(doc) &&
                        !doc.getStepName().equals("Проблемные")) {
            fieldGroup.setEditable(false);
            filesTableRemoveButton.setVisible(false);
        }


        WorkflowInstance instance = service.getWorkflowInstance(doc);
        if (instance != null) {
            workflowInstanceCommentsDs.refresh(
                    ParamsMap.of("instanceId", instance.getId())
            );
        }

        multiUpload.addQueueUploadCompleteListener(() -> {
            for (Map.Entry<UUID, String> entry : multiUpload.getUploadsMap().entrySet()) {
                UUID fileId = entry.getKey();
                String fileName = entry.getValue();
                FileDescriptor fd = fileUploadingAPI.getFileDescriptor(fileId, fileName);
                // save file to FileStorage
                try {
                    fileUploadingAPI.putFileIntoStorage(fileId, fd);
                } catch (FileStorageException e) {
                    throw new RuntimeException("Error saving file to FileStorage", e);
                }

                // save file descriptor to database
                FileDescriptor committedFd = dataSupplier.commit(fd);

                // save to attachedFiles in document
                attachedFilesDs.addItem(committedFd);
            }
            StringBuilder resultString = new StringBuilder();
            for (String value :
                    multiUpload.getUploadsMap().values()) {
                resultString.append("\n").append(value);
            }
            showNotification(
                    String.format(
                            getMessage("attached.uploaded"),
                            resultString.toString()),
                    NotificationType.HUMANIZED
            );
            multiUpload.clearUploads();

            // refresh datasource to show changes
            attachedFilesDs.commit();

        });
    }

    public void download() {
        if (filesTable.getSelected().size() > 0) {
            for (FileDescriptor fileDescriptor : filesTable.getSelected()) {
                AppConfig.createExportDisplay(this).show(fileDescriptor);
            }
        }
    }

    public enum OpenType {
        create,
        edit,
        browse
    }
}