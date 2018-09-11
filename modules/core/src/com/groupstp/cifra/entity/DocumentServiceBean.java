package com.groupstp.cifra.entity;


import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(DocumentService.NAME)
public class DocumentServiceBean implements DocumentService {

    private final int NUMBER_OF_TOP_TAGS = 3;

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
    public void issueDocument(Document doc, Employee emp) {
        journalService.makeMovement(doc, EventType.ISSUE, null,null, emp);
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
        Set<Document> result= new HashSet<>();
        try  {
            Transaction tx = persistence.createTransaction();
            // get EntityManager for the current transaction
            EntityManager em = persistence.getEntityManager();
            // create and execute Query
            Query query = em.createQuery(
                    "select  j.responsible.name,d from cifra$Journal j  join j.doc d where d.docStatus=:status order by j.updateTs desc ");
            query.setParameter("status", DocStatus.ISSUED);


            List qresult =  query.getResultList();//.getFirstResult();
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

    @Override
    public List<Tag> requestTopTags(){
        Transaction tx = persistence.createTransaction();
        EntityManager em = persistence.getEntityManager();
        Query query = em.createQuery(
                "select o from cifra$Tag o join o.documents doc group by o.id order by count(o.id) desc");
        return query.setMaxResults(NUMBER_OF_TOP_TAGS).getResultList();
    }
}