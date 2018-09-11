package com.groupstp.cifra.entity;


import java.util.List;

public interface CheckListService {
    String NAME = "cifra_CheckListService";

    List<CheckList> fillCheckList(Document doc);

}