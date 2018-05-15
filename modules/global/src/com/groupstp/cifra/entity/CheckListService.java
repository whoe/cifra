package com.groupstp.cifra.entity;


import java.util.List;

public interface CheckListService {
    String NAME = "cifra_CheckListService";

    public List<CheckList> fillCheckList(Document doc);

    public void clearCheckList(Document doc);
}