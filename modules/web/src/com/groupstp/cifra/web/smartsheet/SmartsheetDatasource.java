package com.groupstp.cifra.web.smartsheet;

import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.gui.data.impl.CustomValueHierarchicalDatasource;
import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.SmartsheetFactory;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;

import java.util.*;

public class SmartsheetDatasource extends CustomValueHierarchicalDatasource {

    private HashMap<Long, SpecialKVEntity> loadedRows;
    private Smartsheet smartsheet;
    private Sheet sheet;

    @Override
    protected Collection<KeyValueEntity> getEntities(Map<String, Object> params) {
        if (!params.containsKey("token")) return null;

        authorize(params.get("token").toString());

        List<Row> rows;

        try {
            Long sheetId = Long.valueOf(params.get("sheetId").toString());
            rows = getRows(sheetId);
        } catch (SmartsheetException e) {
            e.printStackTrace();
            return null;
        }

        if (rows != null) {
            loadedRows = new HashMap<>();
            Collection<KeyValueEntity> result = new HashSet<>();
            rows.forEach(row -> result.add(rowToKeyValueEntity(row)));

            return result;
        }

        return null;

    }

    private void authorize(String token) {
        smartsheet = SmartsheetFactory.createDefaultClient(token);
    }

    private List<Row> getRows(Long sheetId) throws SmartsheetException {
        sheet = getSheet(sheetId);
        return sheet.getRows();
    }

    private Sheet getSheet(Long sheetId) throws SmartsheetException {
        return smartsheet.sheetResources().getSheet(sheetId, null, null, null, null, null, null, null);
    }

    private SpecialKVEntity rowToKeyValueEntity(Row row) {
        Long parentId = row.getParentId();
        Long id = row.getId();
        SpecialKVEntity entity = new SpecialKVEntity(this);
        entity.setIdName("id");
        entity.setValue("id", id);
        entity.setValue("parent", parentId != null ? loadedRows.get(parentId) : null);
        row.getCells().forEach(cell -> {
            String title = sheet.getColumnById(cell.getColumnId()).getTitle();
            entity.setValue(title, cell.getValue());
        });
        loadedRows.put(row.getId(), entity);
        return entity;
    }


}

