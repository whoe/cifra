package com.groupstp.cifra.listener;

import com.groupstp.cifra.entity.Document;
import com.groupstp.cifra.entity.DocumentService;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("cifra_DocumentListener")
public class DocumentListener implements BeforeUpdateEntityListener<Document> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private DocumentService documentService;

    @Override
    public void onBeforeUpdate(Document entity, EntityManager entityManager) {
        documentService.CheckState(entity);
    }

}