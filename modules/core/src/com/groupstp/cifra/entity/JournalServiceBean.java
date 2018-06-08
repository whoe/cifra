package com.groupstp.cifra.entity;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;
import com.haulmont.cuba.core.Persistence;
import javax.inject.Inject;

@Service(JournalService.NAME)
public class JournalServiceBean implements JournalService {


    @Inject
    private DataManager dataManager;

    @Inject
    private Persistence persistence;

    @Override
    public void makeMovement(Document doc, EventType e, Warehouse w, String address, Employee staff) {
        Journal journal = new Journal();
        journal.setEventType(e);
        journal.setWarehouse(w);
        journal.setCell(doc.getCell());
        journal.setResponsible(staff);

        journal.addDocument(doc);
        dataManager.commit(journal);

    }

}