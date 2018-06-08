package com.groupstp.cifra.entity;



import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.haulmont.cuba.core.Persistence;
import javax.inject.Inject;
import javax.xml.crypto.Data;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(DocumentService.NAME)
public class DocumentServiceBean implements DocumentService {
    @Inject
    private EmployeeService employeeService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private DataManager dataManager;

    @Inject
    private JournalService journalService;

    @Inject
    private Persistence persistence;



    @Override
    public void ArchiveDocument(Document doc) {
        journalService.makeMovement(doc, EventType.ARCHIVE, doc.getWarehouse(),null,employeeService.getCurrentUserEmployee());
        doc.setGotOriginal(true);
        CheckState(doc);
        dataManager.commit(doc);
    }

    @Override
    public void issueDocument(Document doc) {
        journalService.makeMovement(doc, EventType.ISSUE, null,null,employeeService.getCurrentUserEmployee());
        doc.setDocStatus(DocStatus.ISSUED);
        dataManager.commit(doc);
    }

    @Override
    public void returnDocument(Document doc){

        journalService.makeMovement(doc, EventType.ARCHIVE, null,null,employeeService.getCurrentUserEmployee());
        doc.setDocStatus(DocStatus.ARCHIVE);
        dataManager.commit(doc);
    }

    @Override
    public Set<Document> getIssuedDocuments() {
        Set result= new HashSet<>();
        try  {
            Transaction tx = persistence.createTransaction();
            // get EntityManager for the current transaction
            EntityManager em = persistence.getEntityManager();
            // create and execute Query
            Query query = em.createQuery(
                    "select  j.responsible.name,d from cifra$Journal j  join j.doc d where d.docStatus=:status order by j.updateTs desc ");
            query.setParameter("status", 40);


            List qresult =  query.getResultList();//.getFirstResult();
            int i=0;i++;
            qresult.forEach(( item)->{
                String name =(String)((Object[])item)[0];
                Document doc=(Document)((Object[])item)[1];
                if(!result.contains(doc))doc.setDestination(name);
                result.add(doc);
            });
            // commit transaction
            tx.commit();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return result;
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