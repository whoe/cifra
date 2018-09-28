package com.groupstp.cifra.entity;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public interface ImportDocs1CService {
    String NAME = "cifra_ImportDocs1CService";

    void ImportDocs1C(String url, String pass, Date dateStart, Date dateEnd) throws Exception;
    void ImportCompanies1C(String url, String pass) throws Exception;
}