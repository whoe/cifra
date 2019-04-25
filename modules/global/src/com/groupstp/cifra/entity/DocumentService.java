package com.groupstp.cifra.entity;


import java.util.List;

public interface DocumentService {
    String NAME = "cifra_DocumentService";

    List<Tag> requestTopTags();

}