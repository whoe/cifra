package com.groupstp.cifra.entity;


import java.util.Collection;
import java.util.List;

public interface DocumentService {
    String NAME = "cifra_DocumentService";

    public void CheckState(Document doc);

    public void ArchiveDocument(Document doc);

    public void issueDocument(Document doc, Employee emp);

    public void returnDocument(Document doc);

    public List<Tag> requestTopTags();

    Collection<Document> getIssuedDocuments();
}