package com.groupstp.cifra.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;

@Service(ImportDocs1CService.NAME)
public class ImportDocs1CServiceBean implements ImportDocs1CService {

    @Inject
    private Sync1CService sync1CService;


    @Override
    public void ImportDocs1C(String url, String pass, Date dateStart, Date dateEnd) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("dateStart", String.format("%1$tY%1$tm%1$td", dateStart));
        params.put("dateEnd", String.format("%1$tY%1$tm%1$td", dateEnd));
        JsonArray data = (JsonArray) sync1CService.getData1C(url + "cifra", pass, params);
        Import imp = new ImportDocuments();
        for (JsonElement e : data) {
            JsonObject o = e.getAsJsonObject();
            String id = o.get("ВнешнийID").getAsString();
            imp.updateInsertEntity(Document.class, id, o);
        }
    }

    @Override
    public void ImportCompanies1C(String url, String pass) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("reference", "Организации");
        params.put("type", "data");
        params.put("attributes", "[\"УникальныйИдентификатор\", \"Наименование\", \"ИНН\", \"НаименованиеПолное\"]");
        JsonArray data = (JsonArray) sync1CService.getData1C(url + "references", pass, params);
        Import imp = new ImportCompanies();
        for (JsonElement e : data) {
            JsonObject o = e.getAsJsonObject();
            String id = o.get("УникальныйИдентификатор").getAsString();
            if ("УникальныйИдентификатор".equals(id))
                continue;
            imp.updateInsertEntity(Company.class, id, o);
        }
    }
}