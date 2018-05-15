package com.groupstp.cifra.entity;


public interface DocumentService {
    String NAME = "cifra_DocumentService";

    public void CheckState(Document doc);

    public void ArchiveDocument(Document doc);

    public void IssueDocument(Document doc);
}