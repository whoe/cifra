package com.groupstp.cifra.entity;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ImportDocs1CService {
    String NAME = "cifra_ImportDocs1CService";

    void ImportDocs1C(String url, String pass) throws Exception;
    void ImportCompanies1C(String url, String pass) throws Exception;
}