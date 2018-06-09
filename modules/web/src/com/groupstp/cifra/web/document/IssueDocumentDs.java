package com.groupstp.cifra.web.document;


import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.DocumentService;
import com.haulmont.cuba.gui.data.impl.CustomGroupDatasource;


import javax.inject.Inject;

import java.util.*;

/**
 * Created by a on 06.06.2018.
 */
public class IssueDocumentDs extends CustomGroupDatasource<Document, UUID> {

    @Inject
    private
    DocumentService documentService;

    @Override
    protected Collection<Document> getEntities(Map<String, Object> params) {
        return documentService.getIssuedDocuments();
    }



    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}