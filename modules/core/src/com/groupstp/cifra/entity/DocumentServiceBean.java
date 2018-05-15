package com.groupstp.cifra.entity;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.xml.crypto.Data;
import java.awt.*;
import java.util.List;

@Service(DocumentService.NAME)
public class DocumentServiceBean implements DocumentService {
    @Inject
    private EmployeeService employeeService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private DataManager dataManager;

    @Inject
    private JournalService journalService;

    @Override
    public void ArchiveDocument(Document doc) {
        //journalService.makeMovement(doc, EventType.ARCHIVE, doc.getWarehouse());
        Journal journal = new Journal();
        journal.setEventType(EventType.ARCHIVE);
        journal.setWarehouse(doc.getWarehouse());
        journal.setCell(doc.getCell());
        journal.setResponsible(employeeService.getCurrentUserEmployee());
        dataManager.commit(journal);
        doc.setGotOriginal(true);
        CheckState(doc);
        dataManager.commit(doc);
    }

    @Override
    public void IssueDocument(Document doc) {

    }

    @Override
    public void CheckState(Document doc) {
        DocStatus s = doc.getDocStatus();
        try {
            getClass().getMethod("check" + s.name(), new Class[]{Document.class}).invoke(this, doc);
        } catch (Exception e) {
            log.debug("no such method " + "check " + s.name());
        }
    }

    public void checkNEW(Document doc) {
        if(!doc.getGotOriginal())
            return;
        DataManager dataManager = AppBeans.get(DataManager.class);
        DocStatus newstatus = DocStatus.ARCHIVE;
        List<CheckList> l = doc.getChecklist();
        for (CheckList i:l) {
            Boolean checked = i.getChecked()==null ? false : i.getChecked();
            if(!checked)
            {
                newstatus = DocStatus.CORRECTIONS_NEEDED;
                break;
            }
        }
        doc.setDocStatus(newstatus);
    }

    public void checkARCHIVE(Document doc) {

    }

    public void checkCORRECTIONS_NEEDED(Document doc)
    {
        DataManager dataManager = AppBeans.get(DataManager.class);
        DocStatus newstatus = DocStatus.ARCHIVE;
        List<CheckList> l = doc.getChecklist();
        for (CheckList i:l) {
            Boolean checked = i.getChecked()==null ? false : i.getChecked();
            if(!checked)
            {
                return;
            }
        }
        doc.setDocStatus(newstatus);
    }

    public void checkISSUED(Document doc) {

    }
}