package com.groupstp.cifra.web.smartsheet;

import com.groupstp.cifra.entity.tasks.Task;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.KeyValueEntity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.gui.settings.Settings;
import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.SmartsheetFactory;
import com.smartsheet.api.models.Cell;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;
import org.dom4j.Element;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SmartsheetExport extends AbstractLookup {

    private static final String TITLE_COLUMN_TASK_NAME = "Имя задачи";
    private static final String TITLE_COLUMN_START_DATE = "Начало";
    private static final String TITLE_COLUMN_END_DATE = "Дата выполнения";
    private static final String TITLE_COLUMN_AUTHOR = "Автор";
    private static final String TITLE_COLUMN_PERFORMER = "Назначено";
    private static final String TITLE_COLUMN_COMMENT = "Комментарии";

    private final SimpleDateFormat dateFormatForSmartsheet = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    private HierarchicalDatasource<KeyValueEntity, Object> smartsheetDataSource;

    @Inject
    private TextField smartsheetId;

    @Inject
    private TextField token;

    @Inject
    private DataManager dataManager;

    //maps for export
    private HashMap<String, Long> columns = new HashMap<>();
    private HashMap<String, Task> tasks = new HashMap<>();

    @Override
    public void ready() {
        super.ready();
        try {
            if (smartsheetId.getValue() != null && token.getRawValue() != null) {
                int successfullyExportedTasks = export();
                showNotification(getMessage("exportFinished") + successfullyExportedTasks, NotificationType.TRAY);
            }
        } catch (SmartsheetException e) {
            showNotification(e.getMessage());
        }

    }

    /**
     * refresh table, show smartsheet's tasks
     */
    public void refresh() {
        Map<String, Object> parametrMap = ParamsMap.of("token", token.getValue(), "sheetId", smartsheetId.getValue());
        smartsheetDataSource.refresh(parametrMap);
    }

    /**
     * Export task for previous day to Smartsheet
     *
     * @throws SmartsheetException
     */
    private int export() throws SmartsheetException {
        Smartsheet smartsheet = SmartsheetFactory.createDefaultClient(token.getValue());
        Sheet sheet = smartsheet.sheetResources().getSheet(smartsheetId.getValue(),
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        //find idx of column with task name
        AtomicInteger taskNameColumnIndex = new AtomicInteger();
        sheet.getColumns().forEach(col -> {
            columns.put(col.getTitle(), col.getId());
            if (TITLE_COLUMN_TASK_NAME.equals(col.getTitle()))
                taskNameColumnIndex.set(col.getIndex());
        });

        //export tasks
        List<Row> rows = smartsheet.sheetResources().rowResources().addRows(smartsheetId.getValue(), taskList());

        //get and set smartsheet id
        rows.forEach(row -> {
            Task task = tasks.get(row.getCells().get(taskNameColumnIndex.get()).getDisplayValue());
            if (task == null)
                return;
            task.setSmartsheetId(row.getId());
            dataManager.commit(task);
        });

        return rows.size();
    }

    /**
     * Build and return list of Row to be added to Smartsheet
     *
     * @return
     */
    private List<Row> taskList() {
        List<Row> rows = new LinkedList<>();

        List<Task> taskFromDb = dataManager.loadList(LoadContext.create(Task.class).setQuery(LoadContext.createQuery(
                "select t from tasks$Task t where t.smartsheetId=:smartsheetId").setParameter("smartsheetId", null)).setView("tasks-full"));

        taskFromDb.forEach(task -> {
            if (task.getTaskTypical().getDescription() == null) return;

            List<Cell> cells = new LinkedList<>();
            cells.add(createCell(TITLE_COLUMN_TASK_NAME, task.getTaskTypical().getDescription()));
            cells.add(createCell(TITLE_COLUMN_AUTHOR, task.getAuthor().getName()));
            cells.add(createCell(TITLE_COLUMN_PERFORMER, task.getPerformer().getName()));


            if (task.getStartDate() != null)
                cells.add(createCell(TITLE_COLUMN_START_DATE, formatDateToSmartsheet(task.getStartDate())));
            if (task.getEndDate() != null)
                cells.add(createCell(TITLE_COLUMN_END_DATE, formatDateToSmartsheet(task.getEndDate())));
            if (task.getComment() != null)
                cells.add(createCell(TITLE_COLUMN_COMMENT, task.getComment()));

            Row row = new Row();
            row.setCells(cells);
            row.setToBottom(true);
            rows.add(row);
            tasks.put(task.getTaskTypical().getDescription(), task);
        });

        return rows;
    }

    /**
     * create one cell
     *
     * @param columnName name of cell's column
     * @param value      value will be sat
     * @return cell
     */
    private Cell createCell(String columnName, Object value) {
        Cell cell = new Cell();
        cell.setColumnId(columns.get(columnName));
        cell.setValue(value);
        cell.setStrict(false);
        return cell;
    }

    /**
     * @param date Date
     * @return String formatted by pattern 'yyyy-MM-dd'
     */
    private String formatDateToSmartsheet(Date date) {
        return dateFormatForSmartsheet.format(date);
    }

    /**
     * This method is called when the screen is closed to save the screen settings to the database.
     */
    @Override
    public void saveSettings() {
        Element element = getSettings().get(this.getId());
        element.addAttribute("token", token.getRawValue());
        element.addAttribute("smartsheetId", smartsheetId.getRawValue());
        getSettings().setModified(true);
        super.saveSettings();
    }

    /**
     * This method is called when the screen is opened to restore settings saved in the database for the current user.
     *
     * @param settings settings object loaded from the database for the current user
     */
    @Override
    public void applySettings(Settings settings) {
        super.applySettings(settings);
        Element element = settings.get(this.getId());
        token.setValue(element.attributeValue("token"));

        smartsheetId.setValue(element.attributeValue("smartsheetId"));

    }

}