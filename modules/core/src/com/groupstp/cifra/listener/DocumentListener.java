package com.groupstp.cifra.listener;

import com.groupstp.cifra.entity.*;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;

@Component("cifra_DocumentListener")
public class DocumentListener implements BeforeInsertEntityListener<Document>, BeforeUpdateEntityListener<Document>{

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private CheckListService checkListService;

    @Inject
    private DocumentService documentService;


    @Inject
    private DataManager dataManager;

    @Override
    public void onBeforeInsert(Document entity, EntityManager entityManager) {
        if(entity.getDocStatus()!=DocStatus.NEW)
            entity.setDocStatus(DocStatus.NEW);
        checkListService.fillCheckList(entity);
    }


    @Override
    public void onBeforeUpdate(Document entity, EntityManager entityManager) {
        documentService.CheckState(entity);
    }

}