package com.groupstp.cifra.entity;


import com.google.gson.JsonElement;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public interface Sync1CService {
    String NAME = "cifra_Sync1CService";

    public JsonElement getData1C(String base, String report, String userpass) throws IOException, NoSuchAlgorithmException;
    public JsonElement getData1C(String base, String report, String userpass, HashMap<String, String> params) throws IOException, NoSuchAlgorithmException;
}