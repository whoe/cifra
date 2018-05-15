package com.groupstp.cifra.entity;

import org.springframework.stereotype.Service;

@Service(JournalService.NAME)
public class JournalServiceBean implements JournalService {

    @Override
    public void makeMovement(Document doc, EventType e, Warehouse w, String address, Employee staff) {

    }
}