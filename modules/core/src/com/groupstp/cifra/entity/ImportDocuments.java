package com.groupstp.cifra.entity;

import com.google.gson.JsonObject;
import com.haulmont.cuba.core.app.UniqueNumbersAPI;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportDocuments extends Import{

    @Override
    void update(String id, JsonObject o) {

    }

    //    {
//        "Объект": "6bcae7da-62ee-11e8-8143-005056b36868",
//            "ПечатнаяФорма": "Путевой лист",
//            "ВнешняяСсылка": "https://drive.google.com/a/groupstp.ru/file/d/1Bq0yenwKnz49p6uu09lksoEchc6iP2kZ/view?usp=drivesdk",
//            "СтатусЦК": "Черновик",
//            "Отсканирован": "Да",
//            "ТипДокумента": "Путевой лист",
//            "ДатаСканирования": "15.06.2018 17:31:10",
//            "Кладовщик": "<Не указан>",
//            "Принят": "Нет",
//            "ДатаПринятия": "01.01.0001 0:00:00",
//            "БухгалтерПриемщик": "",
//            "ФайлСохраненНаЯндексДиск": "Нет",
//            "ВнешнийID": "6bcae7da-62ee-11e8-8143-005056b36868_1",
//            "ПроверкаПроизведена": "Нет",
//            "КИсправлению": "Нет"
//    },

    @Inject
    private UniqueNumbersAPI uniqueNumbers;

    @Override
    void insert(String id, JsonObject o) throws Exception {
        Document doc = AppBeans.get(Metadata.class).create(Document.class);
        doc.setDocStatus(DocStatus.NEW);
        doc.setExternalId(o.get("ВнешнийID").getAsString());
        doc.setDocType(DocType.findType(o.get("ПечатнаяФорма").getAsString()));
        doc.setDate(new Date());
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
        doc.setDateLoad(f.parse(o.get("ДатаСканирования").getAsString()));
        doc.setGotOriginal(o.get("Отсканирован").getAsString().equals("Да"));
        doc.setExternalLink(o.get("ВнешняяСсылка").getAsString());
        doc.setCompany(Company.find(o.get("Компания").getAsString()));
        AppBeans.get(DataManager.class).commit(doc);
    }
}
