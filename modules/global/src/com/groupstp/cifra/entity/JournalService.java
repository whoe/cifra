package com.groupstp.cifra.entity;


public interface JournalService {
    String NAME = "cifra_JournalService";

    void makeMovement(Document doc, EventType e, Warehouse w, String address, Employee staff);
}