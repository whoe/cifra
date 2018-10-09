package com.groupstp.cifra.service;


import com.groupstp.cifra.entity.Document;

public interface TaskService {
    String NAME = "cifra_TaskService";

    boolean isItActiveTaskForDocument(Document document);
}